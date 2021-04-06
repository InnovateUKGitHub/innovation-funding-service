package org.innovateuk.ifs.testdata.services;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.testdata.builders.data.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.competition.resource.FundingRules.SUBSIDY_CONTROL;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.SMALL;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;
import static org.innovateuk.ifs.testdata.builders.ApplicationDataBuilder.newApplicationData;
import static org.innovateuk.ifs.testdata.builders.ApplicationFinanceDataBuilder.newApplicationFinanceData;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.testdata.builders.ProcurementMilestoneDataBuilder.newProcurementMilestoneDataBuilder;
import static org.innovateuk.ifs.testdata.builders.QuestionResponseDataBuilder.newApplicationQuestionResponseData;
import static org.innovateuk.ifs.testdata.builders.QuestionnaireResponseDataBuilder.newQuestionnaireResponseDataBuilder;
import static org.innovateuk.ifs.testdata.builders.SubsidyBasisDataBuilder.newSubsidyBasisDataBuilder;
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

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDataBuilderService.class);

    @Autowired
    private GenericApplicationContext applicationContext;

    private ApplicationDataBuilder applicationDataBuilder;
    private CompetitionDataBuilder competitionDataBuilder;
    private ApplicationFinanceDataBuilder applicationFinanceDataBuilder;
    private ProcurementMilestoneDataBuilder procurementMilestoneDataBuilder;
    private QuestionResponseDataBuilder questionResponseDataBuilder;
    private QuestionnaireResponseDataBuilder questionnaireResponseDataBuilder;
    private SubsidyBasisDataBuilder subsidyBasisDataBuilder;

    @PostConstruct
    public void readCsvs() {

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);

        applicationDataBuilder = newApplicationData(serviceLocator);
        competitionDataBuilder = newCompetitionData(serviceLocator);
        applicationFinanceDataBuilder = newApplicationFinanceData(serviceLocator);
        questionResponseDataBuilder = newApplicationQuestionResponseData(serviceLocator);
        procurementMilestoneDataBuilder = newProcurementMilestoneDataBuilder(serviceLocator);
        questionnaireResponseDataBuilder = newQuestionnaireResponseDataBuilder(serviceLocator);
        subsidyBasisDataBuilder = newSubsidyBasisDataBuilder(serviceLocator);
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
        else if (applicationLine.markQuestionsComplete || applicationLine.submittedDate != null) {

            Long competitionId = applicationData.getCompetition().getId();

            List<QuestionResource> competitionQuestions = retrieveCachedQuestionsByCompetitionId(competitionId);

            List<QuestionResource> questionsToAnswer = simpleFilter(competitionQuestions, q ->
                    !q.getMultipleStatuses() &&
                    q.getMarkAsCompletedEnabled() &&
                    q.getQuestionSetupType().hasFormInputResponses());

            List<QuestionResponseDataBuilder> responseBuilders = simpleMap(questionsToAnswer, question -> {

                String answerValue = "This is the applicant response for " + question.getName().toLowerCase() + ".";
                String leadApplicantEmail = applicationData.getLeadApplicant().getEmail();

                QuestionResponseDataBuilder responseBuilder = baseBuilder.
                        forQuestion(question.getName()).
                        withAssignee(leadApplicantEmail);

                List<FormInputResource> formInputs = retrieveCachedFormInputsByQuestionId(question);

                if (formInputs.stream().anyMatch(fi -> fi.getScope().equals(FormInputScope.APPLICATION) && fi.getType().equals(FormInputType.TEXTAREA))) {
                    responseBuilder = responseBuilder.withAnswer(answerValue, leadApplicantEmail);
                }

                if (formInputs.stream().anyMatch(fi -> fi.getScope().equals(FormInputScope.APPLICATION) && fi.getType().equals(FormInputType.MULTIPLE_CHOICE))) {
                    FormInputResource multipleChoice = formInputs.stream().filter(fi -> fi.getType().equals(FormInputType.MULTIPLE_CHOICE)).findFirst().get();
                    List<MultipleChoiceOptionResource> choices = multipleChoice.getMultipleChoiceOptions();

                    String applicationName = applicationData.getApplication().getName();
                    String questionName = question.getName();

                    //Pick a choice based on the application name and question name. Ensures we have a random choice, but is the same choice each time generator is ran.
                    int choice = (applicationName + questionName).length() % choices.size();

                    responseBuilder = responseBuilder.withChoice(choices.get(choice), leadApplicantEmail);
                }

                if (formInputs.stream().anyMatch(fi -> fi.getScope().equals(FormInputScope.APPLICATION) && fi.getType().equals(FormInputType.FILEUPLOAD))) {

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
            List<ApplicationOrganisationFinanceBlock> applicationFinanceLines,
            List<ExternalUserLine> externalUsers) {

        if (!applicationLine.createFinanceResponses) {
            return emptyList();
        }

        Map<String, String> usersOrganisations = simpleToMap(externalUsers, user -> user.emailAddress, user -> user.organisationName);

        List<String> applicants = combineLists(applicationLine.leadApplicant, applicationLine.collaborators);

        List<Triple<String, String, OrganisationTypeEnum>> organisations = simpleMap(applicants, email -> {

            UserResource user = retrieveUserByEmail(email);
            OrganisationResource organisation = organisationByName(usersOrganisations.get(email));

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

            if (applicationData.getCompetition().applicantShouldUseJesFinances(organisationType)) {

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
                                organisationName,
                                organisationType)
                );
            }

        });

        return simpleMap(builders, BaseBuilder::build);
    }

    private List<QuestionnaireResponseLine> defaultApplicationQuestionnaireResponseLines(ApplicationData applicationData,
                                                                                                  ApplicationLine applicationLine,
                                                                                                  List<ExternalUserLine> externalUsers){
        if (applicationLine.markQuestionsComplete && applicationData.getCompetition().getFundingRules().equals(SUBSIDY_CONTROL)){
            // Generate the default for the application.
            return uniqueOrganisations(applicationLine, externalUsers).stream()
                    .map(organisation -> new QuestionnaireResponseLine(
                            organisation.getLeft(),
                            applicationData.getCompetition().getName(),
                            applicationData.getApplication().getName(),
                            organisation.getMiddle(),
                            SUBSIDY_BASIS,
                            asList("No", "No")))
                    .collect(toList());
        }
        return emptyList();
    }

    private List<QuestionnaireResponseLine> specifiedQuestionnaireResponseLines(ApplicationLine applicationLine,
                                                                                  List<QuestionnaireResponseLine> questionnaireResponseLines){
        return questionnaireResponseLines.stream()
                .filter(line -> line.competitionName.equals(applicationLine.competitionName))
                .filter(line -> line.applicationName.equals(applicationLine.title))
                .collect(toList());
    }

    public List<QuestionnaireResponseData> createQuestionnaireResponse(ApplicationData applicationData,
                                                                       ApplicationLine applicationLine,
                                                                       List<QuestionnaireResponseLine> questionnaireResponseLines,
                                                                       List<ExternalUserLine> externalUsers) {
        List<QuestionnaireResponseLine> specifiedLines = specifiedQuestionnaireResponseLines(applicationLine, questionnaireResponseLines);
        List<QuestionnaireResponseLine> defaultLines = defaultApplicationQuestionnaireResponseLines(applicationData, applicationLine, externalUsers);
        // Override defaults.
        List<QuestionnaireResponseLine> lines = defaultLines.stream()
                .map(defaultLine -> specifiedLines.stream()
                        .filter(specifiedLine -> defaultLine.organisationName.equals(specifiedLine.organisationName))
                        .findFirst()
                        .orElse(defaultLine))
                .collect(toList());
        return lines.stream()
                .map(line -> questionnaireResponseDataBuilder
                        .withCompetition(applicationData.getCompetition())
                        .withQuestionSetup(line.questionSetupType)
                        .withApplication(applicationData.getApplication())
                        .withOrganisationName(line.organisationName)
                        .withUser(line.user)
                        .withSelectedOptions(line.options)
                        .withQuestionnaireResponse())
                .map(QuestionnaireResponseDataBuilder::build).collect(toList());
    }

    public List<SubsidyBasisData> createSubsidyBasis(ApplicationLine applicationLine,
                                                     List<QuestionnaireResponseData> questionnaireResponseData) {
        return  questionnaireResponseData.stream()
                .filter(line -> line.getQuestionSetupType().equals(SUBSIDY_BASIS) &&
                        line.getCompetition().getName().equals(applicationLine.competitionName) &&
                        line.getApplication().getName().equals(applicationLine.title))
                .map(line -> subsidyBasisDataBuilder
                        .withCompetition(line.getCompetition())
                        .withQuestionnaireResponseUuid(line.getQuestionnaireResponseUuid())
                        .withApplication(line.getApplication())
                        .withOrganisationName(line.getOrganisationName())
                        .withUser(line.getUser())
                        .withOutcome(line.getOutcome())
                        .withSubsidyBasis())
                .map(SubsidyBasisDataBuilder::build).collect(toList());
    }

    private List<Triple<String, String, OrganisationTypeEnum>> uniqueOrganisations(ApplicationLine applicationLine, List<ExternalUserLine> externalUsers){
        Map<String, String> usersOrganisations = simpleToMap(externalUsers, user -> user.emailAddress, user -> user.organisationName);

        List<String> applicants = combineLists(applicationLine.leadApplicant, applicationLine.collaborators);

        List<Triple<String, String, OrganisationTypeEnum>> organisations = simpleMap(applicants, email -> {

            UserResource user = retrieveUserByEmail(email);
            OrganisationResource organisation = organisationByName(usersOrganisations.get(email));

            return Triple.of(user.getEmail(), organisation.getName(),
                    OrganisationTypeEnum.getFromId(organisation.getOrganisationType()));
        });

        return simpleFilter(organisations, triple ->
                isUniqueOrFirstDuplicateOrganisation(triple, organisations));
    }

    public List<ProcurementMilestoneData> createProcurementMilestones(
            ApplicationData applicationData,
            ApplicationLine applicationLine,
            List<ExternalUserLine> externalUsers) {

        if (applicationData.getCompetition().isProcurement() && applicationLine.createFinanceResponses) {

            List<Triple<String, String, OrganisationTypeEnum>> uniqueOrganisations = uniqueOrganisations(applicationLine, externalUsers);

            List<ProcurementMilestoneDataBuilder> builders = simpleMap(uniqueOrganisations, orgDetails -> {
                String user = orgDetails.getLeft();
                String organisationName = orgDetails.getMiddle();

                return procurementMilestoneDataBuilder
                        .withApplication(applicationData.getApplication())
                        .withCompetition(applicationData.getCompetition())
                        .withOrganisation(organisationName)
                        .withUser(user)
                        .withMilestones();
            });
            return simpleMap(builders, BaseBuilder::build);
        } else {
            return emptyList();
        }
    }


    public void completeApplication(
            ApplicationData applicationData,
            ApplicationLine applicationLine,
            List<ApplicationQuestionResponseData> questionResponseData,
            List<ApplicationFinanceData> financeData) {

        if (applicationLine.markQuestionsComplete) {
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
                markApplicationDetailsComplete(applicationLine.markQuestionsComplete).
                markEdiComplete(applicationLine.markQuestionsComplete).
                markApplicationTeamComplete(applicationLine.markQuestionsComplete).
                markResearchCategoryComplete(applicationLine.markQuestionsComplete);
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

        if (!competition.getCompetition().isKtp() && asList(CompetitionStatus.PROJECT_SETUP, CompetitionStatus.ASSESSOR_FEEDBACK).contains(competitionLine.getCompetitionStatus())) {

            basicCompetitionInformation.
                    moveCompetitionIntoFundersPanelStatus().
                    sendFundingDecisions(createFundingDecisionsFromCsv(competitionLine.getName(), applicationLines)).
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
            ApplicationLine line,
            List<InviteLine> inviteLines,
            List<ExternalUserLine> externalUsers) {

        UserResource leadApplicant = retrieveUserByEmail(line.leadApplicant);

        Map<String, String> usersOrganisations = simpleToMap(externalUsers, user -> user.emailAddress, user -> user.organisationName);
        Organisation org = organisationRepository.findOneByName(usersOrganisations.get(line.leadApplicant));

        ApplicationDataBuilder baseBuilder = applicationDataBuilder.withCompetition(competition.getCompetition()).
                withBasicDetails(leadApplicant, line.title, line.researchCategory, line.resubmission, org.getId()).
                withInnovationArea(line.innovationArea).
                withStartDate(line.startDate).
                withDurationInMonths(line.durationInMonths);

        for (String collaborator : line.collaborators) {
            baseBuilder = baseBuilder.inviteCollaborator(retrieveUserByEmail(collaborator), organisationRepository.findOneByName(usersOrganisations.get(collaborator)));
        }

        if (competition.getCompetition().isKtp() && line.submittedDate != null) {
            baseBuilder = baseBuilder.inviteKta();
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
                return builder.withGrantClaim(BigDecimal.valueOf(Integer.valueOf(financeRow.metadata.get(0))));
            case "Organisation size":
                return builder.withOrganisationSize(OrganisationSize.findById(Long.valueOf(financeRow.metadata.get(0))));
            case "Work postcode":
                return builder.withWorkPostcode(financeRow.metadata.get(0));
            case "Fec model enabled":
                return builder.withFecEnabled(Boolean.valueOf(financeRow.metadata.get(0)));
            case "Fec file uploaded":
                return builder.withUploadedFecFile();
            case "Labour":
                return builder.withLabourEntry(
                        financeRow.metadata.get(0),
                        Integer.valueOf(financeRow.metadata.get(1)),
                        Integer.valueOf(financeRow.metadata.get(2)));
            case "Overheads":
                switch (financeRow.metadata.get(0).toLowerCase()) {
                    case "total":
                        return builder.withAdministrationSupportCostsTotalRate(
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
            case "Associate Employment":
                return builder.withAssociateSalaryCosts(
                        financeRow.metadata.get(0),
                        Integer.valueOf(financeRow.metadata.get(1)),
                        bi(financeRow.metadata.get(2))
                );
            case "Associate development":
                return builder.withAssociateDevelopmentCosts(
                        financeRow.metadata.get(0),
                        Integer.valueOf(financeRow.metadata.get(1)),
                        bi(financeRow.metadata.get(2))
                );
            case "Ktp Travel and subsistence":
                return builder.withKtpTravel(
                        KtpTravelCost.KtpTravelCostType.valueOf(financeRow.metadata.get(0)),
                        financeRow.metadata.get(1),
                        bd(financeRow.metadata.get(2)),
                        Integer.valueOf(financeRow.metadata.get(3)));
            case "Consumables":
                return builder.withConsumables(
                        financeRow.metadata.get(0),
                        bi(financeRow.metadata.get(1)),
                        Integer.valueOf(financeRow.metadata.get(2))
                );
            case "Additional company costs":
                return builder.withAdditionalCompanyCosts(
                        AdditionalCompanyCost.AdditionalCompanyCostType.valueOf(financeRow.metadata.get(0)),
                        financeRow.metadata.get(1),
                        bi(financeRow.metadata.get(2))
                );
            case "Academic and secretarial support":
                return builder.withAcademicAndSecretarialSupport(
                        bi(financeRow.metadata.get(0))
                );
            case "Indirect costs":
                return builder.withIndirectCosts(
                        bi(financeRow.metadata.get(0))
                );
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
            String organisationName,
            OrganisationTypeEnum organisationType) {

        UnaryOperator<IndustrialCostDataBuilder> costBuilder = costs -> {
            final IndustrialCostDataBuilder[] builder = {costs};

            Consumer<? super FinanceRowType> costPopulator = type -> {
                switch(type) {
                    case LABOUR:
                        builder[0] = builder[0].withWorkingDaysPerYear(123).
                                withLabourEntry("Role 1", 200, 200).
                                withLabourEntry("Role 2", 400, 300).
                                withLabourEntry("Role 3", 600, 365);
                        break;
                    case OVERHEADS:
                        builder[0] = builder[0].withAdministrationSupportCostsNone();
                        break;
                    case PROCUREMENT_OVERHEADS:
                        builder[0] = builder[0].withProcurementOverheads("procurement overhead" , 1000, 2000);
                        break;
                    case MATERIALS:
                        builder[0] = builder[0].withMaterials("Generator", bd("10020"), 10);
                        break;
                    case CAPITAL_USAGE:
                        builder[0] = builder[0].withCapitalUsage(12, "Depreciating Stuff", true, bd("2120"), bd("1200"), 60);
                        break;
                    case SUBCONTRACTING_COSTS:
                        builder[0] = builder[0].withSubcontractingCost("Developers", "UK", "To develop stuff", bd("90000"));
                        break;
                    case TRAVEL:
                        builder[0] = builder[0].withTravelAndSubsistence("To visit colleagues", 15, bd("398"));
                        break;
                    case OTHER_COSTS:
                        builder[0] = builder[0].withOtherCosts("Some more costs", bd("1100"));
                        break;
                    case VAT:
                        builder[0] = builder[0].withVat(true);
                        break;
                    case FINANCE:
                        if (!competition.isFullyFunded()) {
                            builder[0] = builder[0].withGrantClaim(BigDecimal.valueOf(30));
                        }
                        break;
                    case GRANT_CLAIM_AMOUNT:
                        builder[0] = builder[0].withGrantClaimAmount(12000);
                        break;
                    case OTHER_FUNDING:
                        if (!competition.isFullyFunded()) {
                            builder[0] = builder[0].withOtherFunding("Lottery", LocalDate.of(2016, 4, 1), bd("2468"));
                        }
                        break;
                    case YOUR_FINANCE:
                        //none for industrial costs.
                        break;
                    case ASSOCIATE_SALARY_COSTS:
                        builder[0] = builder[0].withAssociateSalaryCosts("role", 4, new BigInteger("6"));
                        break;
                    case ASSOCIATE_DEVELOPMENT_COSTS:
                        builder[0] = builder[0].withAssociateDevelopmentCosts("role", 4, new BigInteger("7"));
                        break;
                    case CONSUMABLES:
                        builder[0] = builder[0].withConsumables("item", new BigInteger("8"), 3);
                        break;
                    case ASSOCIATE_SUPPORT:
                        builder[0] = builder[0].withAssociateSupport("supp", new BigInteger("13"));
                        break;
                    case KNOWLEDGE_BASE:
                        builder[0] = builder[0].withKnowledgeBase("desc", new BigInteger("15"));
                        break;
                    case ESTATE_COSTS:
                        builder[0] = builder[0].withEstateCosts("desc", new BigInteger("16"));
                        break;
                    case KTP_TRAVEL:
                        builder[0] = builder[0].withKtpTravel(KtpTravelCost.KtpTravelCostType.ASSOCIATE, "desc", new BigDecimal("17.00"), 1);
                        break;
                    case ADDITIONAL_COMPANY_COSTS:
                        builder[0] = builder[0].withAdditionalCompanyCosts(AdditionalCompanyCost.AdditionalCompanyCostType.ASSOCIATE_SALARY, "desc", new BigInteger("18"));
                        break;
                    case PREVIOUS_FUNDING:
                        builder[0] = builder[0].withPreviousFunding("a", "b", "c", new BigDecimal("23"));
                        break;
                    case ACADEMIC_AND_SECRETARIAL_SUPPORT:
                        builder[0] = builder[0].withAcademicAndSecretarialSupport(new BigInteger("18"));
                        break;
                    case INDIRECT_COSTS:
                        builder[0] = builder[0].withIndirectCosts(new BigInteger("20"));
                        break;
                }
            };


            if (competition.isKtp()) {
                if (OrganisationTypeEnum.KNOWLEDGE_BASE == organisationType) {
                    getFinanceRowTypes(competition, true).forEach(costPopulator);
                }
            } else {
                competition.getFinanceRowTypes().forEach(costPopulator);

                if (TRUE.equals(competition.getIncludeProjectGrowthTable())) {
                    builder[0] = builder[0].withProjectGrowthTable(YearMonth.of(2020, 1),
                            60L,
                            new BigDecimal("100000"),
                            new BigDecimal("200000"),
                            new BigDecimal("300000"),
                            new BigDecimal("400000"));
                } else {
                    builder[0] = builder[0].withEmployeesAndTurnover(50L,
                            new BigDecimal("700000"));
                }
            }

            if (competition.getFundingRules() == SUBSIDY_CONTROL) {
                if (organisationName.equals("Northern Irish Ltd.")) {
                    builder[0] = builder[0]
                            .withNorthernIrelandDeclaration(true);
                } else {
                    builder[0] = builder[0]
                            .withNorthernIrelandDeclaration(false);
                }

            }

            if (organisationType == OrganisationTypeEnum.KNOWLEDGE_BASE) {
                return builder[0].withOrganisationSize(SMALL)
                        .withLocation()
                        .withFecEnabled(true)
                        .withUploadedFecFile();
            } else {
                return builder[0].withOrganisationSize(SMALL)
                        .withLocation();
            }
        };

        return applicationFinanceDataBuilder.
                withApplication(application).
                withCompetition(competition).
                withOrganisation(organisationName).
                withUser(user).
                withIndustrialCosts(costBuilder);
    }

    private List<FinanceRowType> getFinanceRowTypes(CompetitionResource competition, Boolean fecModelEnabled) {
        return competition.getFinanceRowTypes().stream()
                .filter(financeRowType -> BooleanUtils.isFalse(fecModelEnabled)
                        ? !FinanceRowType.getFecSpecificFinanceRowTypes().contains(financeRowType)
                        : !FinanceRowType.getNonFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());
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
                        withGrantClaim(BigDecimal.valueOf(100)).
                        withOtherFunding("Lottery", LocalDate.of(2016, 4, 1), bd("2468")).
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
                        withLocation());
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

    private BigInteger bi(String value) {
        return new BigInteger(value);
    }
}
