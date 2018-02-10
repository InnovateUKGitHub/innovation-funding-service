package org.innovateuk.ifs.testdata.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.tuple.Triple;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.testdata.builders.data.ApplicationData;
import org.innovateuk.ifs.testdata.builders.data.ApplicationFinanceData;
import org.innovateuk.ifs.testdata.builders.data.ApplicationQuestionResponseData;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.testdata.ListenableFutureToCompletableFutureHelper.future;
import static org.innovateuk.ifs.testdata.builders.ApplicationDataBuilder.newApplicationData;
import static org.innovateuk.ifs.testdata.builders.ApplicationFinanceDataBuilder.newApplicationFinanceData;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.testdata.builders.QuestionResponseDataBuilder.newApplicationQuestionResponseData;
import static org.innovateuk.ifs.testdata.services.CsvUtils.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * TODO DW - document this class
 */
@Component
public class ApplicationDataBuilderService extends BaseDataBuilderService {

    private static Cache<Long, List<QuestionResource>> questionsByCompetitionId = CacheBuilder.newBuilder().build();

    private static Cache<Long, List<FormInputResource>> formInputsByQuestionId = CacheBuilder.newBuilder().build();

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private QuestionService questionService;

    private ApplicationDataBuilder applicationDataBuilder;
    private CompetitionDataBuilder competitionDataBuilder;
    private ApplicationFinanceDataBuilder applicationFinanceDataBuilder;
    private QuestionResponseDataBuilder questionResponseDataBuilder;

    private List<CsvUtils.ApplicationLine> applicationLines;
    private static List<CsvUtils.ApplicationQuestionResponseLine> questionResponseLines;
    private static List<CsvUtils.ApplicationOrganisationFinanceBlock> applicationFinanceLines;
    private static List<CsvUtils.InviteLine> inviteLines;

    private Function<ApplicationData, CompletableFuture<ApplicationData>> fillInAndCompleteApplicationFn = applicationData -> {

        CompletableFuture<List<ApplicationQuestionResponseData>> questionResponses = future(taskExecutor.submitListenable(() ->
                createApplicationQuestionResponses(applicationData)));

        CompletableFuture<List<ApplicationFinanceData>> applicationFinances = future(taskExecutor.submitListenable(() ->
                createApplicationFinances(applicationData)));

        CompletableFuture<Void> allQuestionsAnswered = CompletableFuture.allOf(questionResponses, applicationFinances);

        CompletableFuture<ApplicationData> completeApplicationFuture = allQuestionsAnswered.thenApplyAsync(done -> {
            List<ApplicationQuestionResponseData> responses = questionResponses.join();
            List<ApplicationFinanceData> finances = applicationFinances.join();
            completeApplication(applicationData, responses, finances);
            return applicationData;
        }, taskExecutor);

        return completeApplicationFuture;
    };

    private Function<CompetitionData, List<CompletableFuture<ApplicationData>>> fillInAndCompleteApplications = competitionData -> {

        List<CompletableFuture<ApplicationData>> applicationFutures = createBasicApplicationDetails(competitionData);

        List<CompletableFuture<ApplicationData>> fillInAndCompleteApplicationFutures = simpleMap(applicationFutures, applicationFuture -> {

            CompletableFuture<ApplicationData> fillInAndCompleteApplicationFuture = applicationFuture.
                    thenComposeAsync(fillInAndCompleteApplicationFn, taskExecutor);

            return fillInAndCompleteApplicationFuture;
        });

        return fillInAndCompleteApplicationFutures;
    };

    @PostConstruct
    public void readCsvs() {
        applicationLines = readApplications();
        inviteLines = readInvites();
        questionResponseLines = readApplicationQuestionResponses();
        applicationFinanceLines = readApplicationFinances();

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);
        applicationDataBuilder = newApplicationData(serviceLocator);
        competitionDataBuilder = newCompetitionData(serviceLocator);
        applicationFinanceDataBuilder = newApplicationFinanceData(serviceLocator);
        questionResponseDataBuilder = newApplicationQuestionResponseData(serviceLocator);
    }

    public List<CompletableFuture<List<ApplicationData>>> fillInAndCompleteApplications(List<CompletableFuture<CompetitionData>> createCompetitionFutures) {

        return simpleMap(createCompetitionFutures, competition -> {

            CompletableFuture<List<CompletableFuture<ApplicationData>>> competitionAndApplicationFutures =
                    competition.thenApplyAsync(fillInAndCompleteApplications, taskExecutor);

            return competitionAndApplicationFutures.thenApply(applicationFutures ->
                    simpleMap(applicationFutures, CompletableFuture::join));
        });
    }

    static List<CsvUtils.ApplicationQuestionResponseLine> readApplicationQuestionResponses() {
        return simpleMap(readCsvLines("application-questions"), CsvUtils.ApplicationQuestionResponseLine::new);
    }

    private List<CompletableFuture<ApplicationData>> createBasicApplicationDetails(CompetitionData competitionData) {

        List<CsvUtils.ApplicationLine> applicationsForCompetition = simpleFilter(applicationLines, applicationLine ->
                applicationLine.competitionName.equals(competitionData.getCompetition().getName()));

        if (applicationsForCompetition.isEmpty()) {
            return emptyList();
        }

        CompetitionDataBuilder basicCompetitionInformation = competitionDataBuilder.withExistingCompetition(competitionData);

        basicCompetitionInformation.moveCompetitionIntoOpenStatus().build();

        ApplicationDataBuilder applicationBuilder = applicationDataBuilder.withCompetition(competitionData.getCompetition());

        List<CompletableFuture<ApplicationData>> createApplicationFutures = simpleMap(applicationsForCompetition, applicationLine -> {

            return future(taskExecutor.submitListenable(() -> {
                return createApplicationFromCsv(applicationBuilder, applicationLine);
            }));
        });

        return createApplicationFutures;
    }

    private List<ApplicationQuestionResponseData> createApplicationQuestionResponses(ApplicationData applicationData) {

        QuestionResponseDataBuilder baseBuilder =
                questionResponseDataBuilder.withApplication(applicationData.getApplication());

        CsvUtils.ApplicationLine applicationLine = simpleFindFirstMandatory(applicationLines, l ->
                l.title.equals(applicationData.getApplication().getName()));

        List<CsvUtils.ApplicationQuestionResponseLine> responsesForApplication =
                simpleFilter(questionResponseLines, r -> r.competitionName.equals(applicationLine.competitionName) && r.applicationName.equals(applicationLine.title));

        // if we have specific answers for questions in the application-questions.csv file, fill them in here now
        if (!responsesForApplication.isEmpty()) {

            List<QuestionResponseDataBuilder> responseBuilders = questionResponsesFromCsv(baseBuilder, applicationLine.leadApplicant, responsesForApplication, applicationData);

            return simpleMap(responseBuilders, BaseBuilder::build);
        }
        // otherwise provide a default set of marked as complete questions if the application is to be submitted
        else if (applicationLine.submittedDate != null) {

            List<QuestionResource> competitionQuestions = retrieveCachedQuestionsByCompetitionId(applicationData.getCompetition().getId());

            List<QuestionResource> questionsToAnswer = simpleFilter(competitionQuestions,
                    q -> !q.getMultipleStatuses() && q.getMarkAsCompletedEnabled() && !"Application details".equals(q.getName()));

            List<QuestionResponseDataBuilder> responseBuilders = simpleMap(questionsToAnswer, question -> {

                QuestionResponseDataBuilder responseBuilder = baseBuilder.
                        forQuestion(question.getName()).
                        withAssignee(applicationData.getLeadApplicant().getEmail()).
                        withAnswer("This is the applicant response for " + question.getName().toLowerCase() + ".", applicationData.getLeadApplicant().getEmail());

                List<FormInputResource> formInputs = retrieveCachedFormInputsByQuestionId(question);

                if (formInputs.stream().anyMatch(fi -> fi.getType().equals(FormInputType.FILEUPLOAD))) {

                    String fileUploadName = (applicationData.getApplication().getName() + "-" + question.getShortName().toLowerCase() + ".pdf")
                            .toLowerCase().replace(' ', '-') ;

                    responseBuilder = responseBuilder.
                            withFileUploads(singletonList(fileUploadName), applicationData.getLeadApplicant().getEmail());
                }

                return responseBuilder;
            });

            return simpleMap(responseBuilders, builder -> builder.build());
        }

        return emptyList();
    }

    private List<ApplicationFinanceData> createApplicationFinances(ApplicationData applicationData) {

        CsvUtils.ApplicationLine applicationLine = simpleFindFirstMandatory(applicationLines, l ->
                l.title.equals(applicationData.getApplication().getName()));

        List<String> applicants = combineLists(applicationLine.leadApplicant, applicationLine.collaborators);

        List<Triple<String, String, OrganisationTypeEnum>> organisations = simpleMap(applicants, email -> {
            UserResource user = retrieveUserByEmail(email);
            OrganisationResource organisation = retrieveOrganisationByUserId(user.getId());
            return Triple.of(user.getEmail(), organisation.getName(), OrganisationTypeEnum.getFromId(organisation.getOrganisationType()));
        });

        List<Triple<String, String, OrganisationTypeEnum>> uniqueOrganisations =
                simpleFilter(organisations, triple -> isUniqueOrFirstDuplicateOrganisation(triple, organisations));

        List<ApplicationFinanceDataBuilder> builders = simpleMap(uniqueOrganisations, orgDetails -> {

            String user = orgDetails.getLeft();
            String organisationName = orgDetails.getMiddle();
            OrganisationTypeEnum organisationType = orgDetails.getRight();

            Optional<CsvUtils.ApplicationOrganisationFinanceBlock> organisationFinances = simpleFindFirst(applicationFinanceLines, finances ->
                    finances.competitionName.equals(applicationLine.competitionName) &&
                            finances.applicationName.equals(applicationLine.title) &&
                            finances.organisationName.equals(organisationName));

            if (organisationType.equals(OrganisationTypeEnum.RESEARCH)) {

                if (organisationFinances.isPresent()) {
                    return generateAcademicFinancesFromSuppliedData(applicationData.getApplication(), applicationData.getCompetition(), user, organisationName, applicationLine.markFinancesComplete);
                } else {
                    return generateAcademicFinances(applicationData.getApplication(), applicationData.getCompetition(), user, organisationName, applicationLine.markFinancesComplete);
                }
            } else {
                if (organisationFinances.isPresent()) {
                    return generateIndustrialCostsFromSuppliedData(applicationData.getApplication(), applicationData.getCompetition(), user, organisationName, organisationFinances.get(), applicationLine.markFinancesComplete);
                } else {
                    return generateIndustrialCosts(applicationData.getApplication(), applicationData.getCompetition(), user, organisationName, applicationLine.markFinancesComplete);
                }
            }

        });

        return simpleMap(builders, BaseBuilder::build);
    }

    private void completeApplication(ApplicationData applicationData, List<ApplicationQuestionResponseData> questionResponseData, List<ApplicationFinanceData> financeData) {

        CsvUtils.ApplicationLine applicationLine = simpleFindFirstMandatory(applicationLines, l ->
                l.title.equals(applicationData.getApplication().getName()));

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
                        withExistingFinances(finance.getApplication(), finance.getCompetition(), finance.getUser(), finance.getOrganisation()).
                        markAsComplete(true, lastElement).
                        build();
            });
        }

        ApplicationDataBuilder applicationBuilder = this.applicationDataBuilder.
                withExistingApplication(applicationData).
                markApplicationDetailsComplete(applicationLine.markDetailsComplete);

        if (applicationLine.submittedDate != null) {
            applicationBuilder = applicationBuilder.submitApplication();
        }

        if (asLinkedSet(ApplicationState.INELIGIBLE, ApplicationState.INELIGIBLE_INFORMED).contains(applicationLine.status)) {
            applicationBuilder = applicationBuilder.markApplicationIneligible(applicationLine.ineligibleReason);
            if (applicationLine.status == ApplicationState.INELIGIBLE_INFORMED) {
                applicationBuilder = applicationBuilder.informApplicationIneligible();
            }
        }

        applicationBuilder.build();
    }


    private List<QuestionResponseDataBuilder> questionResponsesFromCsv(QuestionResponseDataBuilder baseBuilder, String leadApplicant, List<CsvUtils.ApplicationQuestionResponseLine> responsesForApplication, ApplicationData applicationData) {

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

    private ApplicationData createApplicationFromCsv(ApplicationDataBuilder builder, CsvUtils.ApplicationLine line) {

        UserResource leadApplicant = retrieveUserByEmail(line.leadApplicant);

        ApplicationDataBuilder baseBuilder = builder.
                withBasicDetails(leadApplicant, line.title, line.researchCategory, line.resubmission).
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

    private boolean isUniqueOrFirstDuplicateOrganisation(Triple<String, String, OrganisationTypeEnum> currentOrganisation, List<Triple<String, String, OrganisationTypeEnum>> organisationList) {
        return organisationList.stream().filter(triple -> triple.getMiddle().equals(currentOrganisation.getMiddle())).findFirst().get().equals(currentOrganisation);
    }

    private IndustrialCostDataBuilder addFinanceRow(IndustrialCostDataBuilder builder, CsvUtils.ApplicationFinanceRow financeRow) {

        switch (financeRow.category) {
            case "Working days per year":
                return builder.withWorkingDaysPerYear(Integer.valueOf(financeRow.metadata.get(0)));
            case "Grant claim":
                return builder.withGrantClaim(Integer.valueOf(financeRow.metadata.get(0)));
            case "Organisation size":
                return builder.withOrganisationSize(Long.valueOf(financeRow.metadata.get(0)));
            case "Labour":
                return builder.withLabourEntry(financeRow.metadata.get(0), Integer.valueOf(financeRow.metadata.get(1)), Integer.valueOf(financeRow.metadata.get(2)));
            case "Overheads":
                switch (financeRow.metadata.get(0).toLowerCase()) {
                    case "custom":
                        return builder.withAdministrationSupportCostsCustomRate(Integer.valueOf(financeRow.metadata.get(1)));
                    case "default":
                        return builder.withAdministrationSupportCostsDefaultRate();
                    case "none":
                        return builder.withAdministrationSupportCostsNone();
                    default:
                        throw new RuntimeException("Unknown rate type " + financeRow.metadata.get(0).toLowerCase());
                }
            case "Materials":
                return builder.withMaterials(financeRow.metadata.get(0), bd(financeRow.metadata.get(1)), Integer.valueOf(financeRow.metadata.get(2)));
            case "Capital usage":
                return builder.withCapitalUsage(Integer.valueOf(financeRow.metadata.get(4)),
                        financeRow.metadata.get(0), Boolean.parseBoolean(financeRow.metadata.get(1)),
                        bd(financeRow.metadata.get(2)), bd(financeRow.metadata.get(3)), Integer.valueOf(financeRow.metadata.get(5)));
            case "Subcontracting":
                return builder.withSubcontractingCost(financeRow.metadata.get(0), financeRow.metadata.get(1), financeRow.metadata.get(2), bd(financeRow.metadata.get(3)));
            case "Travel and subsistence":
                return builder.withTravelAndSubsistence(financeRow.metadata.get(0), Integer.valueOf(financeRow.metadata.get(1)), bd(financeRow.metadata.get(2)));
            case "Other costs":
                return builder.withOtherCosts(financeRow.metadata.get(0), bd(financeRow.metadata.get(1)));
            case "Other funding":
                return builder.withOtherFunding(financeRow.metadata.get(0), LocalDate.parse(financeRow.metadata.get(1), DATE_PATTERN), bd(financeRow.metadata.get(2)));
            default:
                throw new RuntimeException("Unknown category " + financeRow.category);
        }
    }

    private ApplicationFinanceDataBuilder generateIndustrialCostsFromSuppliedData(ApplicationResource application, CompetitionResource competition, String user, String organisationName, CsvUtils.ApplicationOrganisationFinanceBlock organisationFinances, boolean markAsComplete) {

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

    private ApplicationFinanceDataBuilder generateIndustrialCosts(ApplicationResource application, CompetitionResource competition, String user, String organisationName, boolean markAsComplete) {
        return applicationFinanceDataBuilder.
                withApplication(application).
                withCompetition(competition).
                withOrganisation(organisationName).
                withUser(user).
                withIndustrialCosts(costs -> costs.
                        withWorkingDaysPerYear(123).
                        withGrantClaim(30).
                        withOtherFunding("Lottery", LocalDate.of(2016, 04, 01), bd("2468")).
                        withLabourEntry("Role 1", 200, 200).
                        withLabourEntry("Role 2", 400, 300).
                        withLabourEntry("Role 3", 600, 365).
                        withAdministrationSupportCostsNone().
                        withMaterials("Generator", bd("10020"), 10).
                        withCapitalUsage(12, "Depreciating Stuff", true, bd("2120"), bd("1200"), 60).
                        withSubcontractingCost("Developers", "UK", "To develop stuff", bd("90000")).
                        withTravelAndSubsistence("To visit colleagues", 15, bd("398")).
                        withOtherCosts("Some more costs", bd("1100")).
                        withOrganisationSize(1L));
    }

    private ApplicationFinanceDataBuilder generateAcademicFinances(ApplicationResource application, CompetitionResource competition, String user, String organisationName, boolean markAsComplete) {
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
                        withUploadedJesForm());
    }

    private ApplicationFinanceDataBuilder generateAcademicFinancesFromSuppliedData(ApplicationResource application, CompetitionResource competition, String user, String organisationName, boolean markAsComplete) {
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

    protected List<QuestionResource> retrieveCachedQuestionsByCompetitionId(Long competitionId) {
        return fromCache(competitionId, questionsByCompetitionId, () ->
                questionService.findByCompetition(competitionId).getSuccessObjectOrThrowException());
    }

    protected List<FormInputResource> retrieveCachedFormInputsByQuestionId(QuestionResource question) {
        return fromCache(question.getId(), formInputsByQuestionId, () ->
                formInputService.findByQuestionId(question.getId()).getSuccessObjectOrThrowException());
    }

    protected<K, V> V fromCache(K key, Cache<K, V> cache, Callable<V> loadingFunction) {
        try {
            return cache.get(key, loadingFunction);
        } catch (ExecutionException e) {
            throw new RuntimeException("Exception encountered whilst reading from Cache", e);
        }
    }
}
