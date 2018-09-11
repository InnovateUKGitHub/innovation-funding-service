package org.innovateuk.ifs.testdata.services;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.testdata.builders.data.ApplicationData;
import org.innovateuk.ifs.testdata.builders.data.ApplicationFinanceData;
import org.innovateuk.ifs.testdata.builders.data.ApplicationQuestionResponseData;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.SMALL;
import static org.innovateuk.ifs.form.resource.QuestionType.LEAD_ONLY;
import static org.innovateuk.ifs.testdata.builders.ApplicationDataBuilder.newApplicationData;
import static org.innovateuk.ifs.testdata.builders.ApplicationFinanceDataBuilder.newApplicationFinanceData;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.testdata.builders.QuestionResponseDataBuilder.newApplicationQuestionResponseData;
import static org.innovateuk.ifs.testdata.services.CsvUtils.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * A service that {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} uses to generate Application data.  While
 * {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} is responsible for gathering CSV information and
 * orchestarting the building of it, this service is responsible for taking the CSV data passed to it and using
 * the appropriate builders to generate and update entities.
 */
@Component
@Lazy
public class ApplicationDataBuilderService extends BaseDataBuilderService {

    @Autowired
    private GenericApplicationContext applicationContext;

    private ApplicationDataBuilder applicationDataBuilder;
    private CompetitionDataBuilder competitionDataBuilder;
    private ApplicationFinanceDataBuilder applicationFinanceDataBuilder;
    private QuestionResponseDataBuilder questionResponseDataBuilder;

    @PostConstruct
    public void readCsvs() {

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);

        applicationDataBuilder = newApplicationData(serviceLocator);
        competitionDataBuilder = newCompetitionData(serviceLocator);
        applicationFinanceDataBuilder = newApplicationFinanceData(serviceLocator);
        questionResponseDataBuilder = newApplicationQuestionResponseData(serviceLocator);
    }

    public List<ApplicationQuestionResponseData> createApplicationQuestionResponses(
            ApplicationData applicationData,
            ApplicationLine applicationLine,
            List<ApplicationQuestionResponseLine> questionResponseLines) {

        QuestionResponseDataBuilder baseBuilder =
                questionResponseDataBuilder.withApplication(applicationData.getApplication());

        if (!applicationLine.createApplicationResponses) {
            return emptyList();
        }

        List<CsvUtils.ApplicationQuestionResponseLine> responsesForApplication =
                simpleFilter(questionResponseLines, r ->
                        r.competitionName.equals(applicationLine.competitionName) &&
                        r.applicationName.equals(applicationLine.title));

        // if we have specific answers for questions in the application-questions.csv file, fill them in here now
        if (!responsesForApplication.isEmpty()) {

            List<QuestionResponseDataBuilder> responseBuilders = questionResponsesFromCsv(
                    baseBuilder,
                    applicationLine.leadApplicant,
                    responsesForApplication);

            return simpleMap(responseBuilders, BaseBuilder::build);
        }
        // otherwise provide a default set of marked as complete questions if the application is to be submitted
        else if (applicationLine.submittedDate != null) {

            Long competitionId = applicationData.getCompetition().getId();

            List<QuestionResource> competitionQuestions = retrieveCachedQuestionsByCompetitionId(competitionId);

            List<QuestionResource> questionsToAnswer = simpleFilter(competitionQuestions, q ->
                    !q.getMultipleStatuses() &&
                    q.getMarkAsCompletedEnabled() &&
                    !LEAD_ONLY.equals(q.getType()));

            List<QuestionResponseDataBuilder> responseBuilders = simpleMap(questionsToAnswer, question -> {

                String answerValue = "This is the applicant response for " + question.getName().toLowerCase() + ".";
                String leadApplicantEmail = applicationData.getLeadApplicant().getEmail();

                QuestionResponseDataBuilder responseBuilder = baseBuilder.
                        forQuestion(question.getName()).
                        withAssignee(leadApplicantEmail).
                        withAnswer(answerValue, leadApplicantEmail);

                List<FormInputResource> formInputs = retrieveCachedFormInputsByQuestionId(question);

                if (formInputs.stream().anyMatch(fi -> fi.getType().equals(FormInputType.FILEUPLOAD))) {

                    String applicationName = applicationData.getApplication().getName();
                    String questionName = question.getShortName().toLowerCase();

                    String fileUploadName = (applicationName + "-" + questionName + ".pdf")
                            .toLowerCase().replace(' ', '-') ;

                    responseBuilder = responseBuilder.
                            withFileUploads(singletonList(fileUploadName), leadApplicantEmail);
                }

                return responseBuilder;
            });

            return simpleMap(responseBuilders, BaseBuilder::build);
        }

        return emptyList();
    }

    public List<ApplicationFinanceData> createApplicationFinances(
            ApplicationData applicationData,
            ApplicationLine applicationLine,
            List<CsvUtils.ApplicationOrganisationFinanceBlock> applicationFinanceLines) {

        if (!applicationLine.createFinanceResponses) {
            return emptyList();
        }

        List<String> applicants = combineLists(applicationLine.leadApplicant, applicationLine.collaborators);

        List<Triple<String, String, OrganisationTypeEnum>> organisations = simpleMap(applicants, email -> {

            UserResource user = retrieveUserByEmail(email);
            OrganisationResource organisation = retrieveOrganisationByUserId(user.getId());

            return Triple.of(user.getEmail(), organisation.getName(),
                    OrganisationTypeEnum.getFromId(organisation.getOrganisationType()));
        });

        List<Triple<String, String, OrganisationTypeEnum>> uniqueOrganisations = simpleFilter(organisations, triple ->
                isUniqueOrFirstDuplicateOrganisation(triple, organisations));

        List<ApplicationFinanceDataBuilder> builders = simpleMap(uniqueOrganisations, orgDetails -> {

            String user = orgDetails.getLeft();
            String organisationName = orgDetails.getMiddle();
            OrganisationTypeEnum organisationType = orgDetails.getRight();

            Optional<CsvUtils.ApplicationOrganisationFinanceBlock> organisationFinances =
                    simpleFindFirst(applicationFinanceLines, finances ->
                        finances.competitionName.equals(applicationLine.competitionName) &&
                        finances.applicationName.equals(applicationLine.title) &&
                        finances.organisationName.equals(organisationName));

            if (organisationType.equals(OrganisationTypeEnum.RESEARCH)) {

                return organisationFinances.map(suppliedFinances ->
                        generateAcademicFinancesFromSuppliedData(
                                applicationData.getApplication(),
                                applicationData.getCompetition(),
                                user,
                                organisationName)
                ).orElseGet(() ->
                        generateAcademicFinances(
                                applicationData.getApplication(),
                                applicationData.getCompetition(),
                                user,
                                organisationName)
                );
            } else {
                return organisationFinances.map(suppliedFinances ->
                        generateIndustrialCostsFromSuppliedData(
                                applicationData.getApplication(),
                                applicationData.getCompetition(),
                                user,
                                organisationName,
                                suppliedFinances)
                ).orElseGet(() ->
                        generateIndustrialCosts(
                                applicationData.getApplication(),
                                applicationData.getCompetition(),
                                user,
                                organisationName)
                );
            }

        });

        return simpleMap(builders, BaseBuilder::build);
    }

    public void completeApplication(
            ApplicationData applicationData,
            ApplicationLine applicationLine,
            List<ApplicationQuestionResponseData> questionResponseData,
            List<ApplicationFinanceData> financeData) {

        if (applicationLine.submittedDate != null) {
            forEachWithIndex(questionResponseData, (i, response) -> {
                boolean lastElement = i == questionResponseData.size() - 1;
                questionResponseDataBuilder.
                        withExistingResponse(response).
                        markAsComplete(lastElement).
                        build();
            });
        }

        if (applicationLine.markFinancesComplete) {
            forEachWithIndex(financeData, (i, finance) -> {
                boolean lastElement = i == financeData.size() - 1;
                applicationFinanceDataBuilder.
                        withExistingFinances(
                                finance.getApplication(),
                                finance.getCompetition(),
                                finance.getUser(),
                                finance.getOrganisation()).
                        markAsComplete(true, lastElement).
                        build();
            });
        }

        ApplicationDataBuilder applicationBuilder = this.applicationDataBuilder.
                withExistingApplication(applicationData).
                markApplicationDetailsComplete(applicationLine.markDetailsComplete).
                markApplicationTeamComplete(applicationLine.markDetailsComplete).
                markResearchCategoryComplete(applicationLine.markDetailsComplete);
        if (applicationLine.submittedDate != null) {
            applicationBuilder = applicationBuilder.submitApplication();
        }

        if (asLinkedSet(ApplicationState.INELIGIBLE, ApplicationState.INELIGIBLE_INFORMED).
                contains(applicationLine.status)) {

            applicationBuilder = applicationBuilder.markApplicationIneligible(applicationLine.ineligibleReason);

            if (applicationLine.status == ApplicationState.INELIGIBLE_INFORMED) {
                applicationBuilder = applicationBuilder.informApplicationIneligible();
            }
        }

        applicationBuilder.build();
    }

    public void createFundingDecisions(
            CompetitionData competition,
            CompetitionLine competitionLine,
            List<ApplicationLine> applicationLines) {

        CompetitionDataBuilder basicCompetitionInformation = competitionDataBuilder.withExistingCompetition(competition);

        if (asList(CompetitionStatus.PROJECT_SETUP, CompetitionStatus.ASSESSOR_FEEDBACK).contains(competitionLine.competitionStatus)) {

            basicCompetitionInformation.
                    moveCompetitionIntoFundersPanelStatus().
                    sendFundingDecisions(createFundingDecisionsFromCsv(competitionLine.name, applicationLines)).
                    build();
        }
    }

    private List<Pair<String, FundingDecision>> createFundingDecisionsFromCsv(
            String competitionName,
            List<ApplicationLine> applicationLines) {

        List<CsvUtils.ApplicationLine> matchingApplications = simpleFilter(applicationLines, a ->
                a.competitionName.equals(competitionName));

        List<CsvUtils.ApplicationLine> applicationsWithDecisions = simpleFilter(matchingApplications, a ->
                asList(ApplicationState.APPROVED, ApplicationState.REJECTED).contains(a.status));

        return simpleMap(applicationsWithDecisions, ma -> {
            FundingDecision fundingDecision = ma.status == ApplicationState.APPROVED ? FundingDecision.FUNDED : FundingDecision.UNFUNDED;
            return Pair.of(ma.title, fundingDecision);
        });
    }

    private List<QuestionResponseDataBuilder> questionResponsesFromCsv(
            QuestionResponseDataBuilder baseBuilder,
            String leadApplicant,
            List<CsvUtils.ApplicationQuestionResponseLine> responsesForApplication) {

        return simpleMap(responsesForApplication, line -> {

            String answeringUser = !isBlank(line.answeredBy) ? line.answeredBy : (!isBlank(line.assignedTo) ? line.assignedTo : leadApplicant);

            UnaryOperator<QuestionResponseDataBuilder> withQuestion = builder -> builder.forQuestion(line.questionName);

            UnaryOperator<QuestionResponseDataBuilder> answerIfNecessary = builder ->
                    !isBlank(line.value) ? builder.withAssignee(answeringUser).withAnswer(line.value, answeringUser)
                            : builder;

            UnaryOperator<QuestionResponseDataBuilder> uploadFilesIfNecessary = builder ->
                    !line.filesUploaded.isEmpty() ?
                            builder.withAssignee(answeringUser).withFileUploads(line.filesUploaded, answeringUser) :
                            builder;

            UnaryOperator<QuestionResponseDataBuilder> assignIfNecessary = builder ->
                    !isBlank(line.assignedTo) ? builder.withAssignee(line.assignedTo) : builder;

            return withQuestion.
                    andThen(answerIfNecessary).
                    andThen(uploadFilesIfNecessary).
                    andThen(assignIfNecessary).
                    apply(baseBuilder);
        });
    }

    public ApplicationData createApplication(
            CompetitionData competition,
            CsvUtils.ApplicationLine line,
            List<InviteLine> inviteLines) {

        UserResource leadApplicant = retrieveUserByEmail(line.leadApplicant);

        long where_do_we_get_the_org_from = 123L;

        ApplicationDataBuilder baseBuilder = applicationDataBuilder.withCompetition(competition.getCompetition()).
                withBasicDetails(leadApplicant, line.title, line.researchCategory, line.resubmission, where_do_we_get_the_org_from).
                withInnovationArea(line.innovationArea).
                withStartDate(line.startDate).
                withDurationInMonths(line.durationInMonths);

        for (String collaborator : line.collaborators) {
            baseBuilder = baseBuilder.inviteCollaborator(retrieveUserByEmail(collaborator));
        }

        List<CsvUtils.InviteLine> pendingInvites = simpleFilter(inviteLines,
                invite -> "APPLICATION".equals(invite.type) && line.title.equals(invite.targetName));

        for (CsvUtils.InviteLine invite : pendingInvites) {
            baseBuilder = baseBuilder.inviteCollaboratorNotYetRegistered(invite.email, invite.hash, invite.name,
                    invite.ownerName);
        }

        if (line.status != ApplicationState.CREATED) {
            baseBuilder = baseBuilder.beginApplication();
        }

        return baseBuilder.build();
    }

    private boolean isUniqueOrFirstDuplicateOrganisation(
            Triple<String, String, OrganisationTypeEnum> currentOrganisation,
            List<Triple<String, String, OrganisationTypeEnum>> organisationList) {

        Triple<String, String, OrganisationTypeEnum> matchingRecord = simpleFindFirstMandatory(organisationList, triple ->
                triple.getMiddle().equals(currentOrganisation.getMiddle()));

        return matchingRecord.equals(currentOrganisation);
    }

    private IndustrialCostDataBuilder addFinanceRow(
            IndustrialCostDataBuilder builder,
            CsvUtils.ApplicationFinanceRow financeRow) {

        switch (financeRow.category) {
            case "Working days per year":
                return builder.withWorkingDaysPerYear(Integer.valueOf(financeRow.metadata.get(0)));
            case "Grant claim":
                return builder.withGrantClaim(Integer.valueOf(financeRow.metadata.get(0)));
            case "Organisation size":
                return builder.withOrganisationSize(OrganisationSize.findById(Long.valueOf(financeRow.metadata.get(0))));
            case "Work postcode":
                return builder.withWorkPostcode(financeRow.metadata.get(0));
            case "Labour":
                return builder.withLabourEntry(
                        financeRow.metadata.get(0),
                        Integer.valueOf(financeRow.metadata.get(1)),
                        Integer.valueOf(financeRow.metadata.get(2)));
            case "Overheads":
                switch (financeRow.metadata.get(0).toLowerCase()) {
                    case "custom":
                        return builder.withAdministrationSupportCostsCustomRate(
                                Integer.valueOf(financeRow.metadata.get(1)));
                    case "default":
                        return builder.withAdministrationSupportCostsDefaultRate();
                    case "none":
                        return builder.withAdministrationSupportCostsNone();
                    default:
                        throw new RuntimeException("Unknown rate type " + financeRow.metadata.get(0).toLowerCase());
                }
            case "Materials":
                return builder.withMaterials(
                        financeRow.metadata.get(0),
                        bd(financeRow.metadata.get(1)),
                        Integer.valueOf(financeRow.metadata.get(2)));
            case "Capital usage":
                return builder.withCapitalUsage(
                        Integer.valueOf(financeRow.metadata.get(4)),
                        financeRow.metadata.get(0),
                        Boolean.parseBoolean(financeRow.metadata.get(1)),
                        bd(financeRow.metadata.get(2)),
                        bd(financeRow.metadata.get(3)),
                        Integer.valueOf(financeRow.metadata.get(5)));
            case "Subcontracting":
                return builder.withSubcontractingCost(
                        financeRow.metadata.get(0),
                        financeRow.metadata.get(1),
                        financeRow.metadata.get(2),
                        bd(financeRow.metadata.get(3)));
            case "Travel and subsistence":
                return builder.withTravelAndSubsistence(
                        financeRow.metadata.get(0),
                        Integer.valueOf(financeRow.metadata.get(1)),
                        bd(financeRow.metadata.get(2)));
            case "Other costs":
                return builder.withOtherCosts(
                        financeRow.metadata.get(0),
                        bd(financeRow.metadata.get(1)));
            case "Other funding":
                return builder.withOtherFunding(
                        financeRow.metadata.get(0),
                        LocalDate.parse(financeRow.metadata.get(1), DATE_PATTERN),
                        bd(financeRow.metadata.get(2)));
            default:
                throw new RuntimeException("Unknown category " + financeRow.category);
        }
    }

    private ApplicationFinanceDataBuilder generateIndustrialCostsFromSuppliedData(
            ApplicationResource application,
            CompetitionResource competition,
            String user,
            String organisationName,
            CsvUtils.ApplicationOrganisationFinanceBlock organisationFinances) {

        ApplicationFinanceDataBuilder finance = this.applicationFinanceDataBuilder.
                withApplication(application).
                withCompetition(competition).
                withOrganisation(organisationName).
                withUser(user);

        List<CsvUtils.ApplicationFinanceRow> financeRows = organisationFinances.rows;

        UnaryOperator<IndustrialCostDataBuilder> costBuilder = costs -> {

            IndustrialCostDataBuilder costsWithData = costs;

            for (CsvUtils.ApplicationFinanceRow financeRow : financeRows) {
                costsWithData = addFinanceRow(costsWithData, financeRow);
            }

            return costsWithData;
        };


        return finance.
                withIndustrialCosts(costBuilder);
    }

    private ApplicationFinanceDataBuilder generateIndustrialCosts(
            ApplicationResource application,
            CompetitionResource competition,
            String user,
            String organisationName) {

        return applicationFinanceDataBuilder.
                withApplication(application).
                withCompetition(competition).
                withOrganisation(organisationName).
                withUser(user).
                withIndustrialCosts(costs -> costs.
                        withWorkingDaysPerYear(123).
                        withGrantClaim(30).
                        withOtherFunding("Lottery", LocalDate.of(2016, 4, 1), bd("2468")).
                        withLabourEntry("Role 1", 200, 200).
                        withLabourEntry("Role 2", 400, 300).
                        withLabourEntry("Role 3", 600, 365).
                        withAdministrationSupportCostsNone().
                        withMaterials("Generator", bd("10020"), 10).
                        withCapitalUsage(12, "Depreciating Stuff", true, bd("2120"), bd("1200"), 60).
                        withSubcontractingCost("Developers", "UK", "To develop stuff", bd("90000")).
                        withTravelAndSubsistence("To visit colleagues", 15, bd("398")).
                        withOtherCosts("Some more costs", bd("1100")).
                        withOrganisationSize(SMALL).
                        withWorkPostcode("AB12 3CD"));
    }

    private ApplicationFinanceDataBuilder generateAcademicFinances(
            ApplicationResource application,
            CompetitionResource competition,
            String user,
            String organisationName) {

        return applicationFinanceDataBuilder.
                withApplication(application).
                withCompetition(competition).
                withOrganisation(organisationName).
                withUser(user).
                withAcademicCosts(costs -> costs.
                        withTsbReference("My REF").
                        withDirectlyIncurredStaff(bd("22")).
                        withDirectlyIncurredTravelAndSubsistence(bd("44")).
                        withDirectlyIncurredOtherCosts(bd("66")).
                        withDirectlyAllocatedInvestigators(bd("88")).
                        withDirectlyAllocatedEstateCosts(bd("110")).
                        withDirectlyAllocatedOtherCosts(bd("132")).
                        withIndirectCosts(bd("154")).
                        withExceptionsStaff(bd("176")).
                        withExceptionsOtherCosts(bd("198")).
                        withUploadedJesForm().
                        withWorkPostcode("AB12 3CD"));
    }

    private ApplicationFinanceDataBuilder generateAcademicFinancesFromSuppliedData(
            ApplicationResource application,
            CompetitionResource competition,
            String user,
            String organisationName) {

        return applicationFinanceDataBuilder.
                withApplication(application).
                withCompetition(competition).
                withOrganisation(organisationName).
                withUser(user).
                withAcademicCosts(costs -> costs.
                        withTsbReference("My REF").
                        withUploadedJesForm());
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
