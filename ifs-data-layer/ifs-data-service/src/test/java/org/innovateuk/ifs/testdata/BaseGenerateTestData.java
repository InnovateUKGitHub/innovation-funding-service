package org.innovateuk.ifs.testdata;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.flywaydb.core.Flyway;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.service.EmailService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.notifications.service.senders.email.EmailNotificationSender;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.sil.experian.resource.AccountDetails;
import org.innovateuk.ifs.sil.experian.resource.SILBankDetails;
import org.innovateuk.ifs.sil.experian.resource.ValidationResult;
import org.innovateuk.ifs.sil.experian.resource.VerificationResult;
import org.innovateuk.ifs.sil.experian.service.SilExperianEndpoint;
import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.testdata.builders.data.ApplicationData;
import org.innovateuk.ifs.testdata.builders.data.ApplicationFinanceData;
import org.innovateuk.ifs.testdata.builders.data.ApplicationQuestionResponseData;
import org.innovateuk.ifs.testdata.builders.data.BaseUserData;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.testdata.CsvUtils.*;
import static org.innovateuk.ifs.testdata.builders.ApplicationDataBuilder.newApplicationData;
import static org.innovateuk.ifs.testdata.builders.ApplicationFinanceDataBuilder.newApplicationFinanceData;
import static org.innovateuk.ifs.testdata.builders.AssessmentDataBuilder.newAssessmentData;
import static org.innovateuk.ifs.testdata.builders.AssessorDataBuilder.newAssessorData;
import static org.innovateuk.ifs.testdata.builders.AssessorInviteDataBuilder.newAssessorInviteData;
import static org.innovateuk.ifs.testdata.builders.AssessorResponseDataBuilder.newAssessorResponseData;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.testdata.builders.CompetitionFunderDataBuilder.newCompetitionFunderData;
import static org.innovateuk.ifs.testdata.builders.ExternalUserDataBuilder.newExternalUserData;
import static org.innovateuk.ifs.testdata.builders.InternalUserDataBuilder.newInternalUserData;
import static org.innovateuk.ifs.testdata.builders.OrganisationDataBuilder.newOrganisationData;
import static org.innovateuk.ifs.testdata.builders.ProjectDataBuilder.newProjectData;
import static org.innovateuk.ifs.testdata.builders.PublicContentDateDataBuilder.newPublicContentDateDataBuilder;
import static org.innovateuk.ifs.testdata.builders.PublicContentGroupDataBuilder.newPublicContentGroupDataBuilder;
import static org.innovateuk.ifs.testdata.builders.QuestionDataBuilder.newQuestionData;
import static org.innovateuk.ifs.testdata.builders.QuestionResponseDataBuilder.newApplicationQuestionResponseData;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Generates web test data based upon csvs in /src/test/resources/testdata using data builders
 */
@ActiveProfiles({"integration-test,seeding-db"})
@DirtiesContext
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
abstract class BaseGenerateTestData extends BaseIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(BaseGenerateTestData.class);

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
    public static final String PROJECT_FINANCE_EMAIL = "lee.bowman@innovateuk.test";

    @Value("${flyway.url}")
    private String databaseUrl;

    @Value("${flyway.user}")
    private String databaseUser;

    @Value("${flyway.password}")
    private String databasePassword;

    @Value("${flyway.locations}")
    private String locations;

    @Value("${flyway.placeholders.ifs.system.user.uuid}")
    private String systemUserUUID;

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private NotificationSender emailNotificationSender;

    @Autowired
    private BankDetailsService bankDetailsService;

    @Autowired
    protected PublicContentRepository publicContentRepository;

    @Autowired
    protected CompetitionRepository competitionRepository;

    @Autowired
    protected CompetitionService competitionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private TestService testService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    private ApplicationDataBuilder applicationDataBuilder;
    private CompetitionDataBuilder competitionDataBuilder;
    private QuestionDataBuilder questionDataBuilder;
    private ApplicationFinanceDataBuilder applicationFinanceDataBuilder;
    private CompetitionFunderDataBuilder competitionFunderDataBuilder;
    private PublicContentGroupDataBuilder publicContentGroupDataBuilder;
    private PublicContentDateDataBuilder publicContentDateDataBuilder;
    private ExternalUserDataBuilder externalUserBuilder;
    private InternalUserDataBuilder internalUserBuilder;
    private OrganisationDataBuilder organisationBuilder;
    private AssessorDataBuilder assessorUserBuilder;
    private AssessorInviteDataBuilder assessorInviteUserBuilder;
    private AssessmentDataBuilder assessmentDataBuilder;
    private AssessorResponseDataBuilder assessorResponseDataBuilder;
    private ProjectDataBuilder projectDataBuilder;
    private QuestionResponseDataBuilder questionResponseDataBuilder;

    private static List<OrganisationLine> organisationLines;

    /**
     * select "Competition", "Application", "Question", "Answer", "File upload", "Answered by", "Assigned to", "Marked as complete" UNION ALL select c.name, a.name, q.name, fir.value, fir.file_entry_id, updater.email, assignee.email, qs.marked_as_complete from competition c join application a on a.competition = c.id join question q on q.competition_id = c.id join form_input fi on fi.question_id = q.id join form_input_type fit on fi.form_input_type_id = fit.id left join form_input_response fir on fir.form_input_id = fi.id left join process_role updaterrole on updaterrole.id = fir.updated_by_id left join user updater on updater.id = updaterrole.user_id join question_status qs on qs.application_id = a.id and qs.question_id = q.id left join process_role assigneerole on assigneerole.id = qs.assignee_id left join user assignee on assignee.id = assigneerole.user_id where fit.title in ('textinput','textarea','date','fileupload','percentage') INTO OUTFILE '/var/lib/mysql-files/application-questions3.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     */
    private static List<CompetitionLine> competitionLines;

    private static List<QuestionLine> questionLines;

    private static List<CompetitionFunderLine> competitionFunderLines;

    private static List<PublicContentGroupLine> publicContentGroupLines;

    private static List<PublicContentDateLine> publicContentDateLines;

    private static List<ApplicationLine> applicationLines;

    /**
     * select "Email", "Hash", "Name", "Status", "Type", "Target", "Owner" UNION ALL select i.email, i.hash, i.name, i.status, i.type, i.target_id, i.owner_id from invite i where not exists (select 1 from user where email = i.email) into OUTFILE '/tmp/invites3.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '';
     */
    private static List<InviteLine> inviteLines;

    /**
     * select "Email","First name","Last name","Status","Organisation name","Organisation type","Organisation address line 1", "Line 2","Line3","Town or city","Postcode","County","Address Type" UNION ALL SELECT u.email, u.first_name, u.last_name, u.status, o.name, ot.name, a.address_line1, a.address_line2, a.address_line3, a.town, a.postcode, a.county, at.name from user u join user_organisation uo on uo.user_id = u.id join organisation o on o.id = uo.organisation_id join organisation_type ot on ot.id = o.organisation_type_id join user_role ur on ur.user_id = u.id and ur.role_id = 4 left join organisation_address oa on oa.organisation_id = o.id left join address a on oa.address_id = a.id left join address_type at on at.id = oa.address_type_id INTO OUTFILE '/var/lib/mysql-files/external-users8.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     */
    private static List<ExternalUserLine> externalUserLines;

    private static List<AssessorUserLine> assessorUserLines;

    /**
     * select "Email","First name","Last name","Status","Organisation name","Organisation type","Organisation address line 1", "Line 2","Line3","Town or city","Postcode","County","Address Type","Role" UNION ALL SELECT u.email, u.first_name, u.last_name, u.status, o.name, ot.name, a.address_line1, a.address_line2, a.address_line3, a.town, a.postcode, a.county, at.name, r.name from user u join user_organisation uo on uo.user_id = u.id join organisation o on o.id = uo.organisation_id join organisation_type ot on ot.id = o.organisation_type_id left join organisation_address oa on oa.organisation_id = o.id left join address a on oa.address_id = a.id left join address_type at on at.id = oa.address_type_id join user_role ur on ur.user_id = u.id and ur.role_id != 4 join role r on ur.role_id = r.id INTO OUTFILE '/var/lib/mysql-files/internal-users3.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     **/
    private static List<InternalUserLine> internalUserLines;

    /**
     * select "Competition", "Application", "Question", "Answer", "File upload", "Answered by", "Assigned to", "Marked as complete" UNION ALL select c.name, a.name, q.name, fir.value, fir.file_entry_id, updater.email, assignee.email, qs.marked_as_complete from competition c join application a on a.competition = c.id join question q on q.competition_id = c.id join form_input fi on fi.question_id = q.id join form_input_type fit on fi.form_input_type_id = fit.id left join form_input_response fir on fir.form_input_id = fi.id left join process_role updaterrole on updaterrole.id = fir.updated_by_id left join user updater on updater.id = updaterrole.user_id left join question_status qs on qs.application_id = a.id and qs.question_id = q.id left join process_role assigneerole on assigneerole.id = qs.assignee_id left join user assignee on assignee.id = assigneerole.user_id where fit.title in ('textinput','textarea','date','fileupload','percentage') order by 1,2,3 INTO OUTFILE '/var/lib/mysql-files/application-questions5.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     * <p>
     * And applied search-and-replace for:
     * <p>
     * How many balls can you juggle?
     * What is the size of the potential market for your project?
     * <p>
     * What do you wear when juggling?
     * How will you exploit and market your project?
     * <p>
     * What is your preferred juggling pattern?
     * What economic, social and environmental benefits do you expect your project to deliver and when?
     * <p>
     * What mediums can you juggle with?
     * What technical approach will you use and how will you manage your project?
     * <p>
     * Fifth Question
     * What is innovative about your project?
     */
    private static List<ApplicationQuestionResponseLine> questionResponseLines;

    /**
     * select "Competition name", "Application name", "Organisation name", "Category" union all select row.compname, row.appname, row.orgname, '' from (select distinct o.name as orgname, a.name as appname, c.name as compname, '' from competition c join application a on a.competition = c.id join process_role pr on pr.role_id in (1,2) and pr.application_id = a.id join organisation o on o.id = pr.organisation_id group by c.name, a.name, o.name) as row  INTO OUTFILE '/var/lib/mysql-files/application-finances.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
     */
    private static List<ApplicationOrganisationFinanceBlock> applicationFinanceLines;

    private static List<AssessmentLine> assessmentLines;

    private static List<AssessorResponseLine> assessorResponseLines;

    private static List<ProjectLine> projectLines;

    @Before
    public void setup() throws Exception {
        if (cleanDbFirst()) {
            freshDb();
        }
    }

    @Before
    public void readCsvs() {
        organisationLines = readOrganisations();
        competitionLines = readCompetitions();
        questionLines = readQuestions();
        competitionFunderLines = readCompetitionFunders();
        publicContentGroupLines = readPublicContentGroups();
        publicContentDateLines = readPublicContentDates();
        applicationLines = readApplications();
        inviteLines = readInvites();
        externalUserLines = readExternalUsers();
        internalUserLines = readInternalUsers();
        assessorUserLines = readAssessorUsers();
        questionResponseLines = readApplicationQuestionResponses();
        applicationFinanceLines = readApplicationFinances();
        assessmentLines = readAssessments();
        assessorResponseLines = readAssessorResponses();
        projectLines = readProjects();
    }

    @PostConstruct
    public void replaceExternalDependencies() {

        IdentityProviderService idpServiceMock = mock(IdentityProviderService.class);
        EmailService emailServiceMock = mock(EmailService.class);
        SilExperianEndpoint silExperianEndpointMock = mock(SilExperianEndpoint.class);

        when(idpServiceMock.createUserRecordWithUid(isA(String.class), isA(String.class))).thenAnswer(
                user -> serviceSuccess(UUID.randomUUID().toString()));
        when(idpServiceMock.activateUser(isA(String.class))).thenAnswer(uuid -> serviceSuccess(uuid));
        when(idpServiceMock.deactivateUser(isA(String.class))).thenAnswer(uuid -> serviceSuccess(uuid));

        when(emailServiceMock.sendEmail(isA(EmailAddress.class), isA(List.class), isA(String.class), isA(String.class), isA(String.class))).
                thenReturn(serviceSuccess(emptyList()));

        when(silExperianEndpointMock.validate(isA(SILBankDetails.class))).thenReturn(serviceSuccess(new ValidationResult(true, "", emptyList())));
        when(silExperianEndpointMock.verify(isA(AccountDetails.class))).thenReturn(serviceSuccess(new VerificationResult("10", "10", "10", "10", emptyList())));

        RegistrationService registrationServiceUnwrapped = (RegistrationService) unwrapProxy(registrationService);
        ReflectionTestUtils.setField(registrationServiceUnwrapped, "idpService", idpServiceMock);

        EmailNotificationSender notificationSenderUnwrapped = (EmailNotificationSender) unwrapProxy(emailNotificationSender);
        ReflectionTestUtils.setField(notificationSenderUnwrapped, "emailService", emailServiceMock);

        BankDetailsService bankDetailsServiceUnwrapped = (BankDetailsService) unwrapProxy(bankDetailsService);
        ReflectionTestUtils.setField(bankDetailsServiceUnwrapped, "silExperianEndpoint", silExperianEndpointMock);
    }

    @PostConstruct
    public void setupBaseBuilders() {

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);

        applicationDataBuilder = newApplicationData(serviceLocator);
        competitionDataBuilder = newCompetitionData(serviceLocator);
        competitionFunderDataBuilder = newCompetitionFunderData(serviceLocator);
        questionDataBuilder = newQuestionData(serviceLocator);
        applicationFinanceDataBuilder = newApplicationFinanceData(serviceLocator);
        externalUserBuilder = newExternalUserData(serviceLocator);
        internalUserBuilder = newInternalUserData(serviceLocator);
        organisationBuilder = newOrganisationData(serviceLocator);
        assessorUserBuilder = newAssessorData(serviceLocator);
        assessorInviteUserBuilder = newAssessorInviteData(serviceLocator);
        assessmentDataBuilder = newAssessmentData(serviceLocator);
        assessorResponseDataBuilder = newAssessorResponseData(serviceLocator);
        projectDataBuilder = newProjectData(serviceLocator);
        publicContentGroupDataBuilder = newPublicContentGroupDataBuilder(serviceLocator);
        publicContentDateDataBuilder = newPublicContentDateDataBuilder(serviceLocator);
        questionResponseDataBuilder = newApplicationQuestionResponseData(serviceLocator);
    }

    @Test
    public void generateTestData() throws ExecutionException, InterruptedException {

        long before = System.currentTimeMillis();

        LOG.info("Starting generating data...");
        System.out.println("Starting generating data...");

        fixUpDatabase();
        createOrganisations();
        createInternalUsers();
        createExternalUsers();
        createCompetitions();

        List<CompletableFuture<ApplicationData>> applicationFutures = createBasicApplicationDetails();

        List<CompletableFuture<CompletableFuture<Void>>> questionResponseFutures = simpleMap(applicationFutures, applicationFuture -> {
            return applicationFuture.thenApplyAsync(applicationData -> {

                CompletableFuture<List<ApplicationQuestionResponseData>> questionResponses = future(taskExecutor.submitListenable(() ->
                        createApplicationQuestionResponses(applicationData)));

                CompletableFuture<List<ApplicationFinanceData>> applicationFinances = future(taskExecutor.submitListenable(() ->
                        createApplicationFinances(applicationData)));

                return CompletableFuture.allOf(combineLists(questionResponses, applicationFinances).toArray(new CompletableFuture[] {})).thenAcceptAsync(done -> {
                    List<ApplicationQuestionResponseData> responses = questionResponses.join();
                    List<ApplicationFinanceData> finances = applicationFinances.join();
                    completeApplication(applicationData, responses, finances);
                });

            }, taskExecutor);
        });

        questionResponseFutures.forEach(f1 -> {
            CompletableFuture<Void> f2 = f1.join();
            f2.join();
        });

        moveCompetitionsToCorrectFinalState();

        Future<?> fundingDecisions = taskExecutor.submit(() -> createFundingDecisions());
        taskExecutor.submit(() -> updateQuestions());
        taskExecutor.submit(() -> createCompetitionFunders());
        taskExecutor.submit(() -> {
            createPublicContentGroups();
            createPublicContentDates();
        });
        taskExecutor.submit(() -> {
            createAssessors();
            createNonRegisteredAssessorInvites();
            createAssessments();
        });

        fundingDecisions.get();
        createProjects();

        long after = System.currentTimeMillis();

        LOG.info("Finished generating data in " + ((after - before) / 1000) + " seconds");
        System.out.println("Finished generating data in " + ((after - before) / 1000) + " seconds");
    }

    private void createFundingDecisions() {

        LOG.info("============ STAGE 6 of X - creating Funding Decisions for Applications =================");

        competitionLines.forEach(line -> {

            Long competitionId = competitionRepository.findByName(line.name).get(0).getId();

            CompetitionDataBuilder basicCompetitionInformation = competitionDataBuilder.withExistingCompetition(competitionId);

            if (line.fundersPanelEndDate != null && line.fundersPanelEndDate.isBefore(ZonedDateTime.now())) {

                basicCompetitionInformation.
                        moveCompetitionIntoFundersPanelStatus().
                        sendFundingDecisions(createFundingDecisionsFromCsv(line.name)).
                        restoreOriginalMilestones().build();
            }
        });

    }

    private List<CompletableFuture<ApplicationData>> createBasicApplicationDetails() {

        LOG.info("============ STAGE 5 of X - creating Applications =================");

        List<String> competitionsToAddApplicationsTo = removeDuplicates(simpleMap(applicationLines, line -> line.competitionName));

        competitionsToAddApplicationsTo.forEach(competitionName -> {

            Long competitionId = competitionRepository.findByName(competitionName).get(0).getId();

            CompetitionDataBuilder basicCompetitionInformation = competitionDataBuilder.withExistingCompetition(competitionId);

            basicCompetitionInformation.moveCompetitionIntoOpenStatus().build();
        });

        List<CompletableFuture<ApplicationData>> createApplicationFutures = simpleMap(applicationLines, line -> {

            return future(taskExecutor.submitListenable(() -> {

                Long competitionId = competitionRepository.findByName(line.competitionName).get(0).getId();

                CompetitionResource competition = doAs(compAdmin(), () -> competitionService.getCompetitionById(competitionId).getSuccessObjectOrThrowException());

                return createApplicationFromCsv(applicationDataBuilder.withCompetition(competition), line);
            }));
        });

        return createApplicationFutures;
    }

    private List<ApplicationQuestionResponseData> createApplicationQuestionResponses(ApplicationData applicationData) {

        QuestionResponseDataBuilder baseBuilder =
                questionResponseDataBuilder.withApplication(applicationData.getApplication());

        ApplicationLine applicationLine = simpleFindFirstMandatory(applicationLines, l ->
                l.title.equals(applicationData.getApplication().getName()));

        List<ApplicationQuestionResponseLine> responsesForApplication =
                simpleFilter(questionResponseLines, r -> r.competitionName.equals(applicationLine.competitionName) && r.applicationName.equals(applicationLine.title));

        // if we have specific answers for questions in the application-questions.csv file, fill them in here now
        if (!responsesForApplication.isEmpty()) {

            List<QuestionResponseDataBuilder> responseBuilders = questionResponsesFromCsv(baseBuilder, applicationLine.leadApplicant, responsesForApplication, applicationData);

            return simpleMap(responseBuilders, BaseBuilder::build);
        }
        // otherwise provide a default set of marked as complete questions if the application is to be submitted
        else if (applicationLine.submittedDate != null) {

            // TODO DW - this was cached in builder before
            List<QuestionResource> competitionQuestions = questionService.findByCompetition(applicationData.getCompetition().getId()).getSuccessObjectOrThrowException();

            List<QuestionResource> questionsToAnswer = simpleFilter(competitionQuestions,
                    q -> !q.getMultipleStatuses() && q.getMarkAsCompletedEnabled() && !"Application details".equals(q.getName()));

            List<QuestionResponseDataBuilder> responseBuilders = simpleMap(questionsToAnswer, question -> {

                QuestionResponseDataBuilder responseBuilder = baseBuilder.
                        forQuestion(question.getName()).
                        withAssignee(applicationData.getLeadApplicant().getEmail()).
                        withAnswer("This is the applicant response for " + question.getName().toLowerCase() + ".", applicationData.getLeadApplicant().getEmail());

                // TODO DW - this was cached in builder before
                List<FormInputResource> formInputs = formInputService.findByQuestionId(question.getId()).getSuccessObjectOrThrowException();

                if (formInputs.stream().anyMatch(fi -> fi.getType().equals(FormInputType.FILEUPLOAD))) {

                    String fileUploadName = (applicationData.getApplication().getName() + "-" + question.getShortName().toLowerCase() + ".pdf")
                            .toLowerCase().replace(' ', '-') ;

                    responseBuilder = responseBuilder.
                            withFileUploads(singletonList(fileUploadName), applicationData.getLeadApplicant().getEmail());
                }

                return responseBuilder;
            });

            return simpleMap(responseBuilders, builder -> {
                return testService.doWithinTransaction(() -> builder.build());
            });
        }

        return emptyList();
    }

    private List<ApplicationFinanceData> createApplicationFinances(ApplicationData applicationData) {

        ApplicationLine applicationLine = simpleFindFirstMandatory(applicationLines, l ->
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

            Optional<ApplicationOrganisationFinanceBlock> organisationFinances = simpleFindFirst(applicationFinanceLines, finances ->
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

        ApplicationLine applicationLine = simpleFindFirstMandatory(applicationLines, l ->
                l.title.equals(applicationData.getApplication().getName()));

        questionResponseData.forEach(response -> {
            // TODO DW - which should we MAC
            questionResponseDataBuilder.
                    withExistingResponse(response).
                    markAsComplete();
        });

        financeData.forEach(finance -> {
            // TODO DW - which should we MAC
            applicationFinanceDataBuilder.
                    withExistingFinances(finance.getApplication(), finance.getCompetition(), finance.getUser()).
                    markAsComplete(true);
        });

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

    private void moveCompetitionsToCorrectFinalState() {

        List<String> competitionsToAddApplicationsTo = removeDuplicates(simpleMap(applicationLines, line -> line.competitionName));

        competitionsToAddApplicationsTo.forEach(competitionName -> {

            Long competitionId = competitionRepository.findByName(competitionName).get(0).getId();

            CompetitionDataBuilder basicCompetitionInformation = competitionDataBuilder.withExistingCompetition(competitionId);

            basicCompetitionInformation.restoreOriginalMilestones().build();
        });
    }

    private void updateQuestions() {

        LOG.info("============ STAGE 7 of X - updating Questions =================");

        questionLines.forEach(this::updateQuestion);
    }

    private void updateQuestion(QuestionLine questionLine) {
        this.questionDataBuilder.updateApplicationQuestionHeading(questionLine.ordinal,
                        questionLine.competitionName,
                        questionLine.heading,
                        questionLine.title,
                        questionLine.subtitle).build();
    }

    private void createProjects() {

        LOG.info("============ STAGE 14 of X - creating Projects =================");

        projectLines.forEach(this::createProject);
    }

    private void createProject(ProjectLine line) {

        ProjectDataBuilder baseBuilder = this.projectDataBuilder.
                withExistingProject(line.name).
                withStartDate(line.startDate);

        UnaryOperator<ProjectDataBuilder> assignProjectManagerIfNecessary =
                builder -> !isBlank(line.projectManager) ? builder.withProjectManager(line.projectManager) : builder;

        UnaryOperator<ProjectDataBuilder> setProjectAddressIfNecessary =
                builder -> line.projectAddressAdded ? builder.withProjectAddressOrganisationAddress() : builder;

        UnaryOperator<ProjectDataBuilder> setMonitoringOfficerIfNecessary =
                builder -> !isBlank(line.moFirstName) ?
                        builder.withMonitoringOfficer(line.moFirstName, line.moLastName, line.moEmail, line.moPhoneNumber) : builder;

        UnaryOperator<ProjectDataBuilder> selectFinanceContactsIfNecessary = builder -> {

            ProjectDataBuilder currentBuilder = builder;

            for (Pair<String, String> fc : line.financeContactsForOrganisations) {
                currentBuilder = currentBuilder.withFinanceContact(fc.getLeft(), fc.getRight());
            }

            return currentBuilder;
        };

        UnaryOperator<ProjectDataBuilder> submitBankDetailsIfNecessary = builder -> {

            ProjectDataBuilder currentBuilder = builder;

            for (Triple<String, String, String> bd : line.bankDetailsForOrganisations) {
                currentBuilder = currentBuilder.withBankDetails(bd.getLeft(), bd.getMiddle(), bd.getRight());
            }

            return currentBuilder;
        };

        testService.doWithinTransaction(() ->
            assignProjectManagerIfNecessary.
                    andThen(setProjectAddressIfNecessary).
                    andThen(setMonitoringOfficerIfNecessary).
                    andThen(selectFinanceContactsIfNecessary).
                    andThen(submitBankDetailsIfNecessary).
                    apply(baseBuilder).
                    build());

    }

    private void createExternalUsers() {

        LOG.info("============ STAGE 3 of X - creating External Users =================");

        externalUserLines.forEach(line -> createUser(externalUserBuilder, line));
    }

    private void createAssessors() {

        LOG.info("============ STAGE 11 of X - creating Assessors =================");

        assessorUserLines.forEach(this::createAssessor);
    }

    private void createNonRegisteredAssessorInvites() {

        LOG.info("============ STAGE 12 of X - creating Assessor Invites =================");

        testService.doWithinTransaction(() -> {
            List<InviteLine> assessorInvites = simpleFilter(inviteLines, invite -> "COMPETITION".equals(invite.type));
            List<InviteLine> nonRegisteredAssessorInvites = simpleFilter(assessorInvites, invite -> !userRepository.findByEmail(invite.email).isPresent());
            nonRegisteredAssessorInvites.forEach(line -> createAssessorInvite(assessorInviteUserBuilder, line));
        });
    }

    private void createAssessments() {

        LOG.info("============ STAGE 13 of X - creating Assessments =================");

        testService.doWithinTransaction(() -> assessmentLines.forEach(this::createAssessment));
        testService.doWithinTransaction(() -> assessorResponseLines.forEach(this::createAssessorResponse));
        testService.doWithinTransaction(() -> assessmentLines.forEach(this::submitAssessment));
    }

    private void createAssessment(AssessmentLine line) {
        assessmentDataBuilder.withAssessmentData(
                line.assessorEmail,
                line.applicationName,
                line.rejectReason,
                line.rejectComment,
                line.state,
                line.feedback,
                line.recommendComment
        )
                .build();
    }

    private void createAssessorResponse(AssessorResponseLine line) {
        assessorResponseDataBuilder.withAssessorResponseData(line.competitionName,
                line.applicationName,
                line.assessorEmail,
                line.shortName,
                line.description,
                line.isResearchCategory,
                line.value)
            .build();
    }

    private void submitAssessment(AssessmentLine line) {
        assessmentDataBuilder.withSubmission(
                line.applicationName,
                line.assessorEmail,
                line.state
        )
                .build();
    }

    private void createCompetitionFunders() {

        LOG.info("============ STAGE 8 of X - creating Competition Funders =================");

        testService.doWithinTransaction(() -> competitionFunderLines.forEach(this::createCompetitionFunder));
    }

    private void createPublicContentGroups() {

        LOG.info("============ STAGE 9 of X - creating Public Content groups =================");

        testService.doWithinTransaction(() -> setDefaultCompAdmin());
        testService.doWithinTransaction(() -> publicContentGroupLines.forEach(this::createPublicContentGroup));
    }

    private void createPublicContentDates() {

        LOG.info("============ STAGE 10 of X - creating Public Content dates =================");

        testService.doWithinTransaction(() -> publicContentDateLines.forEach(this::createPublicContentDate));
    }

    private void createCompetitionFunder(CompetitionFunderLine line) {
        competitionFunderDataBuilder.withCompetitionFunderData(line.competitionName, line.funder, line.funder_budget, line.co_funder)
                .build();
    }

    private void createPublicContentGroup(PublicContentGroupLine line) {
        publicContentGroupDataBuilder.withPublicContentGroup(line.competitionName, line.heading, line.content, line.section)
                .build();
    }

    private void createPublicContentDate(PublicContentDateLine line) {
        publicContentDateDataBuilder.withPublicContentDate(line.competitionName, line.date, line.content)
                .build();
    }

    private void createOrganisations() {

        LOG.info("============ STAGE 1 of X - creating Organisations =================");

        List<Future<?>> futures = simpleMap(organisationLines, line -> {

            return taskExecutor.submit(() -> {

                OrganisationDataBuilder organisation =
                        organisationBuilder.createOrganisation(line.name, line.companyRegistrationNumber, lookupOrganisationType(line.organisationType));

                for (OrganisationAddressType organisationType : line.addressType) {
                    organisation = organisation.withAddress(organisationType,
                            line.addressLine1, line.addressLine2,
                            line.addressLine3, line.town,
                            line.postcode, line.county);
                }

                organisation.build();
            });
        });

        waitForFuturesToComplete(futures);
    }

    private void createInternalUsers() {

        LOG.info("============ STAGE 2 of X - creating Internal Users =================");

        internalUserLines.forEach(line -> {

            testService.doWithinTransaction(() -> {

                setDefaultSystemRegistrar();

                List<UserRoleType> roles = simpleMap(line.roles, UserRoleType::fromName);

                InternalUserDataBuilder baseBuilder = internalUserBuilder.withRoles(roles);

                createUser(baseBuilder, line);
            });
        });
    }

    private void createCompetitions() {

        LOG.info("============ STAGE 4 of X - creating Competitions =================");

        List<Future<?>> futures = simpleMap(competitionLines, line -> {

            return taskExecutor.submit(() -> {

                testService.doWithinTransaction(this::setDefaultCompAdmin);

                Optional<Long> existingCompetitionIdWithName =
                        "Connected digital additive manufacturing".equals(line.name) ? Optional.of(1L) : Optional.empty();

                createCompetition(line, existingCompetitionIdWithName);
            });
        });

        waitForFuturesToComplete(futures);
    }

    private List<Pair<String, FundingDecision>> createFundingDecisionsFromCsv(String competitionName) {
        List<ApplicationLine> matchingApplications = simpleFilter(applicationLines, a -> a.competitionName.equals(competitionName));
        List<ApplicationLine> applicationsWithDecisions = simpleFilter(matchingApplications, a -> asList(ApplicationState.APPROVED, ApplicationState.REJECTED).contains(a.status));
        return simpleMap(applicationsWithDecisions, ma -> Pair.of(ma.title, ma.status == ApplicationState.APPROVED ? FundingDecision.FUNDED : FundingDecision.UNFUNDED));
    }

    private void createCompetition(CompetitionLine competitionLine, Optional<Long> competitionId) {
        competitionBuilderWithBasicInformation(competitionLine, competitionId).build();
    }

    private List<QuestionResponseDataBuilder> questionResponsesFromCsv(QuestionResponseDataBuilder baseBuilder, String leadApplicant, List<ApplicationQuestionResponseLine> responsesForApplication, ApplicationData applicationData) {

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

    private ApplicationData createApplicationFromCsv(ApplicationDataBuilder builder, ApplicationLine line) {

        UserResource leadApplicant = retrieveUserByEmail(line.leadApplicant);

        ApplicationDataBuilder baseBuilder = builder.
                withBasicDetails(leadApplicant, line.title, line.researchCategory, line.resubmission).
                withInnovationArea(line.innovationArea).
                withStartDate(line.startDate).
                withDurationInMonths(line.durationInMonths);

        for (String collaborator : line.collaborators) {
            baseBuilder = baseBuilder.inviteCollaborator(retrieveUserByEmail(collaborator));
        }

        List<InviteLine> pendingInvites = simpleFilter(BaseGenerateTestData.inviteLines,
                invite -> "APPLICATION".equals(invite.type) && line.title.equals(invite.targetName));

        for (InviteLine invite : pendingInvites) {
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

    private IndustrialCostDataBuilder addFinanceRow(IndustrialCostDataBuilder builder, ApplicationFinanceRow financeRow) {

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

    private ApplicationFinanceDataBuilder generateIndustrialCostsFromSuppliedData(ApplicationResource application, CompetitionResource competition, String user, String organisationName, ApplicationOrganisationFinanceBlock organisationFinances, boolean markAsComplete) {

        ApplicationFinanceDataBuilder finance = this.applicationFinanceDataBuilder.
                withApplication(application).
                withCompetition(competition).
                withOrganisation(organisationName).
                withUser(user);

        List<ApplicationFinanceRow> financeRows = organisationFinances.rows;

        UnaryOperator<IndustrialCostDataBuilder> costBuilder = costs -> {

            IndustrialCostDataBuilder costsWithData = costs;

            for (ApplicationFinanceRow financeRow : financeRows) {
                costsWithData = addFinanceRow(costsWithData, financeRow);
            }

            return costsWithData;
        };


        return finance.
                withIndustrialCosts(costBuilder);
//                markAsComplete(markAsComplete);
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
                        withOrganisationSize(1L)).
                markAsComplete(markAsComplete);
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
                        withUploadedJesForm()).
                markAsComplete(markAsComplete);
    }

    private ApplicationFinanceDataBuilder generateAcademicFinancesFromSuppliedData(ApplicationResource application, CompetitionResource competition, String user, String organisationName, boolean markAsComplete) {
        return applicationFinanceDataBuilder.
                withApplication(application).
                withCompetition(competition).
                withOrganisation(organisationName).
                withUser(user).
                withAcademicCosts(costs -> costs.
                        withTsbReference("My REF").
                        withUploadedJesForm()).
                markAsComplete(markAsComplete);
    }

    private CompetitionDataBuilder competitionBuilderWithBasicInformation(CompetitionLine line, Optional<Long> existingCompetitionId) {
        CompetitionDataBuilder basicInformation;
                if (line.nonIfs) {
                    basicInformation = nonIfsCompetitionDataBuilder(line);
                } else {
                    basicInformation = ifsCompetitionDataBuilder(line, existingCompetitionId);
                }

        return line.setupComplete ? basicInformation.withSetupComplete() : basicInformation;
    }

    private CompetitionDataBuilder nonIfsCompetitionDataBuilder(CompetitionLine line) {
        return competitionDataBuilder
                .createNonIfsCompetition()
                .withBasicData(line.name, null, line.innovationAreas,
                        line.innovationSector, null, null, null,
                        null, null, null, null, null, null, null, null, null,
                        null, emptyList(), null, null, line.nonIfsUrl)
                .withOpenDate(line.openDate)
                .withSubmissionDate(line.submissionDate)
                .withFundersPanelEndDate(line.fundersPanelEndDate)
                .withReleaseFeedbackDate(line.releaseFeedback)
                .withRegistrationDate(line.registrationDate)
                .withPublicContent(line.published, line.shortDescription, line.fundingRange, line.eligibilitySummary,
                        line.competitionDescription, line.fundingType, line.projectSize, line.keywords, line.inviteOnly);
    }

    private CompetitionDataBuilder ifsCompetitionDataBuilder(CompetitionLine line, Optional<Long> existingCompetitionId) {
        return existingCompetitionId.map(id -> competitionDataBuilder.
                withExistingCompetition(1L).
                withBasicData(line.name, line.type, line.innovationAreas,
                        line.innovationSector, line.researchCategory, line.leadTechnologist, line.compExecutive,
                        line.budgetCode, line.pafCode, line.code, line.activityCode, line.assessorCount, line.assessorPay, line.hasAssessmentPanel, line.hasInterviewStage,
                        line.multiStream, line.collaborationLevel, line.leadApplicantTypes, line.researchRatio, line.resubmission, null).
                withNewMilestones().
                withFundersPanelEndDate(line.fundersPanelEndDate).
                withReleaseFeedbackDate(line.releaseFeedback).
                withFeedbackReleasedDate(line.feedbackReleased).
                withPublicContent(line.published, line.shortDescription, line.fundingRange, line.eligibilitySummary,
                        line.competitionDescription, line.fundingType, line.projectSize, line.keywords, line.inviteOnly)

        ).orElse(competitionDataBuilder.
                createCompetition().
                withBasicData(line.name, line.type, line.innovationAreas,
                        line.innovationSector, line.researchCategory, line.leadTechnologist, line.compExecutive,
                        line.budgetCode, line.pafCode, line.code, line.activityCode, line.assessorCount, line.assessorPay, line.hasAssessmentPanel, line.hasInterviewStage,
                        line.multiStream, line.collaborationLevel, line.leadApplicantTypes, line.researchRatio, line.resubmission, null).
                withApplicationFormFromTemplate().
                withNewMilestones()).
                withOpenDate(line.openDate).
                withBriefingDate(line.briefingDate).
                withSubmissionDate(line.submissionDate).
                withAllocateAssesorsDate(line.allocateAssessorDate).
                withAssessorBriefingDate(line.assessorBriefingDate).
                withAssessorAcceptsDate(line.assessorAcceptsDate).
                withAssessorsNotifiedDate(line.assessorsNotifiedDate).
                withAssessorEndDate(line.assessorEndDate).
                withAssessmentClosedDate(line.assessmentClosedDate).
                withLineDrawDate(line.drawLineDate).
                withAsessmentPanelDate(line.assessmentPanelDate).
                withPanelDate(line.panelDate).
                withFundersPanelDate(line.fundersPanelDate).
                withFundersPanelEndDate(line.fundersPanelEndDate).
                withReleaseFeedbackDate(line.releaseFeedback).
                withFeedbackReleasedDate(line.feedbackReleased).
                withPublicContent(line.published, line.shortDescription, line.fundingRange, line.eligibilitySummary,
                line.competitionDescription, line.fundingType, line.projectSize, line.keywords, line.inviteOnly);
    }

    private void freshDb() throws Exception {
        try {
            cleanAndMigrateDatabaseWithPatches(locations.split(","));
        } catch (Exception e) {
            fail("Exception thrown migrating with script directories: " + locations.split(",") + e.getMessage());
        }
    }

    private Object unwrapProxy(Object services) {
        try {
            return unwrapProxies(asList(services)).get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Object> unwrapProxies(Collection<Object> services) {
        List<Object> unwrappedProxies = new ArrayList<>();
        for (Object service : services) {
            if (AopUtils.isJdkDynamicProxy(service)) {
                try {
                    unwrappedProxies.add(((Advised) service).getTargetSource().getTarget());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                unwrappedProxies.add(service);
            }
        }
        return unwrappedProxies;
    }

    private void cleanAndMigrateDatabaseWithPatches(String[] patchLocations) {
        Map<String, String> placeholders = ImmutableMap.of("ifs.system.user.uuid", systemUserUUID);
        Flyway f = new Flyway();
        f.setDataSource(databaseUrl, databaseUser, databasePassword);
        f.setLocations(patchLocations);
        f.setPlaceholders(placeholders);
        f.clean();
        f.migrate();
    }

    protected UserResource compAdmin() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException());
    }

    protected OrganisationResource retrieveOrganisationById(Long id) {
        return doAs(systemRegistrar(), () -> organisationService.findById(id).getSuccessObjectOrThrowException());
    }

    protected OrganisationResource retrieveOrganisationByUserId(Long id) {
        return doAs(systemRegistrar(), () -> organisationService.getPrimaryForUser(id).getSuccessObjectOrThrowException());
    }

    protected UserResource systemRegistrar() {
        return newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build();
    }

    private <T> T doAs(UserResource user, Supplier<T> action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            return action.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    private void doAs(UserResource user, Runnable action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    private void createAssessor(AssessorUserLine line) {

        List<InviteLine> assessorInvitesForThisAssessor = simpleFilter(inviteLines, invite ->
                invite.email.equals(line.emailAddress) && invite.type.equals("COMPETITION"));

        AssessorDataBuilder builder = assessorUserBuilder;

        Optional<User> existingUser = userRepository.findByEmail(line.emailAddress);
        Optional<User> sentBy = userRepository.findByEmail("john.doe@innovateuk.test");
        Optional<ZonedDateTime> sentOn = Optional.of(ZonedDateTime.now());

        for (InviteLine invite : assessorInvitesForThisAssessor) {
            builder = builder.withInviteToAssessCompetition(
                    invite.targetName,
                    invite.email,
                    invite.name,
                    invite.hash,
                    invite.status,
                    existingUser,
                    invite.innovationAreaName,
                    sentBy,
                    sentOn
            );
        }

        String inviteHash = !isBlank(line.hash) ? line.hash : UUID.randomUUID().toString();
        String innovationArea = !line.innovationAreas.isEmpty() ? line.innovationAreas.get(0) : "";

        AssessorDataBuilder baseBuilder = builder.withInviteToAssessCompetition(
                line.competitionName,
                line.emailAddress,
                line.firstName + " " + line.lastName,
                inviteHash,
                line.inviteStatus,
                existingUser,
                innovationArea,
                sentBy,
                sentOn
        );

        if (!existingUser.isPresent()) {
            baseBuilder = baseBuilder.registerUser(
                    line.firstName,
                    line.lastName,
                    line.emailAddress,
                    line.phoneNumber,
                    line.ethnicity,
                    line.gender,
                    line.disability,
                    inviteHash
            );
        } else {
            baseBuilder = baseBuilder.addAssessorRole();
        }

        baseBuilder = baseBuilder.addSkills(line.skillAreas, line.businessType, line.innovationAreas);
        baseBuilder = baseBuilder.addAffiliations(
                line.principalEmployer,
                line.role,
                line.professionalAffiliations,
                line.appointments,
                line.financialInterests,
                line.familyAffiliations,
                line.familyFinancialInterests
        );

        if (line.agreementSigned) {
            baseBuilder = baseBuilder.addAgreementSigned();
        }

        if (!line.rejectionReason.isEmpty()) {
            baseBuilder = baseBuilder.rejectInvite(inviteHash, line.rejectionReason, line.rejectionComment);
        } else if (InviteStatus.OPENED.equals(line.inviteStatus)) {
            baseBuilder = baseBuilder.acceptInvite(inviteHash);
        }

        baseBuilder.build();
    }

    private void createAssessorInvite(AssessorInviteDataBuilder assessorInviteUserBuilder, InviteLine line) {
        assessorInviteUserBuilder.withInviteToAssessCompetition(
                line.targetName,
                line.email,
                line.name,
                line.hash,
                line.status,
                userRepository.findByEmail(line.email),
                line.innovationAreaName,
                userRepository.findByEmail(line.sentByEmail),
                Optional.of(line.sentOn)
        ).
                build();
    }

    private <T extends BaseUserData, S extends BaseUserDataBuilder<T, S>> void createUser(S baseBuilder, UserLine line) {

        Function<S, S> registerUserIfNecessary = builder -> builder.registerUser(line.firstName, line.lastName, line.emailAddress, line.organisationName, line.phoneNumber);

        Function<S, S> verifyEmail = builder -> builder.verifyEmail();

        Function<S, S> inactivateUserIfNecessary = builder -> !(line.emailVerified) ? builder.deactivateUser() : builder;

        registerUserIfNecessary.andThen(verifyEmail).andThen(inactivateUserIfNecessary).apply(baseBuilder).build();
    }

    private OrganisationTypeEnum lookupOrganisationType(String organisationType) {
        return OrganisationTypeEnum.valueOf(organisationType.toUpperCase().replace(" ", "_"));
    }

    protected abstract boolean cleanDbFirst();

    protected abstract void fixUpDatabase();

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    protected void setDefaultSystemRegistrar() {
        setLoggedInUser(newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build());
        testService.doWithinTransaction(() ->
                setLoggedInUser(userService.findByEmail(BaseDataBuilder.IFS_SYSTEM_REGISTRAR_USER_EMAIL).getSuccessObjectOrThrowException())
        );
    }

    protected void setDefaultCompAdmin() {
        setLoggedInUser(newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build());
        testService.doWithinTransaction(() ->
                setLoggedInUser(userService.findByEmail(COMP_ADMIN_EMAIL).getSuccessObjectOrThrowException())
        );
    }

    private void waitForFuturesToComplete(List<? extends Future<?>> futures) {
        futures.forEach(f -> {
            try {
                f.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * This method converts a ListenableFuture into a CompletableFuture.  CompletableFutures are useful to us because
     * it allows us to chain futures together to produce new compound Futures.
     *
     * Originally from https://blog.krecan.net/2014/06/11/converting-listenablefutures-to-completablefutures-and-back/
     */
    protected <T> CompletableFuture<T> future(ListenableFuture<T> listenableFuture) {

        //create an instance of CompletableFuture
        CompletableFuture<T> completable = new CompletableFuture<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                // propagate cancel to the listenable future
                boolean result = listenableFuture.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return result;
            }
        };

        // add callback
        listenableFuture.addCallback(new ListenableFutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                completable.complete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                completable.completeExceptionally(t);
            }
        });
        return completable;
    }
}
