package org.innovateuk.ifs.testdata;

import com.google.common.collect.ImmutableMap;
import org.flywaydb.core.Flyway;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.service.EmailService;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.core.transactional.ProjectToBeCreatedService;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplication;
import org.innovateuk.ifs.sil.crm.resource.SilLoanAssessment;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.sil.experian.resource.AccountDetails;
import org.innovateuk.ifs.sil.experian.resource.SILBankDetails;
import org.innovateuk.ifs.sil.experian.resource.ValidationResult;
import org.innovateuk.ifs.sil.experian.resource.VerificationResult;
import org.innovateuk.ifs.sil.experian.service.SilExperianEndpoint;
import org.innovateuk.ifs.testdata.builders.data.*;
import org.innovateuk.ifs.testdata.services.*;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.testdata.data.CompetitionWebTestData.buildCompetitionLines;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.COMP_ADMIN_EMAIL;
import static org.innovateuk.ifs.testdata.services.CsvUtils.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Generates web test data based upon CSVs in /src/test/resources/testdata, using Services of different data areas to
 * generate the data.
 *
 * It it this class' responsibility to gather the information from the various CSVs and orchestrate the build of this
 * data using the services available.
 *
 * This test class accepts some optional parameters to define what needs to be built and how:
 *
 * 1) ifs.generate.test.data.execution=(SINGLE_THREADED|MULTI_THREADED)
 *
 *    This defines whether to generate the data in sequence or in parallel.  MULTI_THREADED is good for development
 *    as it is much faster.  SINGLE_THREADED is used to generate final dumps as it is consistent in its ordering.
 *
 * 2) ifs.generate.test.data.competition.filter=(ALL_COMPETITIONS|NO_COMPETITIONS|BY_NAME)
 *
 *    This defines which Competitions to generate (and all competition-specific data under them).  ALL_COMPETITIONS
 *    and NO_COMPETITIONS are self-explanatory.  BY_NAME will generate a single Competition by name - if using this
 *    parameter, we need to also supply "ifs.generate.test.data.competition.filter.name".
 *
 * 3) ifs.generate.test.data.competition.filter.name
 *
 *    In conjunction with "ifs.generate.test.data.competition.filter=BY_NAME", this parameter allows you to specify a
 *    single Competition to generate.
 *
 *    It looks as though spring starts up before we run table generation so it will fail the schema validation. So set
 *    spring.jpa.hibernate.ddl-auto=none for this scenario only.
 */
@ActiveProfiles({"integration-test","seeding-db"})
@DirtiesContext
@SpringBootTest(classes = GenerateTestDataConfiguration.class,
        properties = {"spring.jpa.hibernate.ddl-auto=none"})
abstract class BaseGenerateTestData extends BaseIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(BaseGenerateTestData.class);

    private static String competitionNameForFilter;

    private enum CompetitionFilter implements Predicate<CompetitionLine> {

        ALL_COMPETITIONS(competitionLine -> true),
        NO_COMPETITIONS(competitionLine -> false),
        BY_NAME(competitionLine -> {
            assert competitionNameForFilter != null;
            return competitionNameForFilter.equals(competitionLine.getName());
        });

        private Predicate<CompetitionLine> test;

        CompetitionFilter(Predicate<CompetitionLine> test) {
            this.test = test;
        }

        @Override
        public boolean test(CompetitionLine competitionLine) {
            return test.test(competitionLine);
        }
    }

    @Value("${ifs.generate.test.data.competition.filter:BY_NAME}")
    private CompetitionFilter competitionFilter;

    @Value("${spring.flyway.url}")
    private String databaseUrl;

    @Value("${spring.flyway.user}")
    private String databaseUser;

    @Value("${spring.flyway.password}")
    private String databasePassword;

    @Value("${spring.flyway.locations}")
    private String locations;

    @Value("${spring.flyway.placeholders.ifs.system.user.uuid}")
    private String systemUserUUID;

    @Value("${ifs.data.service.file.storage.base}")
    private String storageLocation;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    private BankDetailsService bankDetailsService;

    @Autowired
    protected CompetitionRepository competitionRepository;

    @Autowired
    private TestService testService;

    @Autowired
    @Qualifier("generateTestDataExecutor")
    private Executor taskExecutor;

    @Autowired
    private CompetitionDataBuilderService competitionDataBuilderService;

    @Autowired
    private ApplicationDataBuilderService applicationDataBuilderService;

    @Autowired
    private AssessmentDataBuilderService assessmentDataBuilderService;

    @Autowired
    private ProjectDataBuilderService projectDataBuilderService;

    @Autowired
    private UserDataBuilderService userDataBuilderService;

    @Autowired
    private OrganisationDataBuilderService organisationDataBuilderService;

    @Autowired
    private SupporterDataService supporterDataService;

    @Autowired
    private ProjectToBeCreatedService projectToBeCreatedService;

    @Autowired
    private CrmService crmService;

    private List<OrganisationLine> organisationLines;
    private List<CompetitionLine> competitionLines;
    private List<CsvUtils.ApplicationLine> applicationLines;
    private List<PublicContentGroupLine> publicContentGroupLines;
    private List<PublicContentDateLine> publicContentDateLines;
    private List<ExternalUserLine> externalUserLines;
    private List<InternalUserLine> internalUserLines;
    private List<CsvUtils.AssessmentLine> assessmentLines;
    private List<CsvUtils.AssessorResponseLine> assessorResponseLines;
    private List<CsvUtils.AssessorUserLine> assessorUserLines;
    private List<CsvUtils.ApplicationQuestionResponseLine> questionResponseLines;
    private List<CsvUtils.ApplicationOrganisationFinanceBlock> applicationFinanceLines;
    private List<CsvUtils.InviteLine> inviteLines;
    private List<CsvUtils.QuestionnaireResponseLine> questionnaireResponseLines;

    @Value("${ifs.generate.test.data.competition.filter.name:Subsidy control comp in assessment}")
    private void setCompetitionFilterName(String competitionNameForFilter) {
       BaseGenerateTestData.competitionNameForFilter = competitionNameForFilter;
    }

    @Before
    public void setup() throws Exception {
        if (cleanDbFirst()) {
            freshDb();
        }
    }

    @Before
    public void readCsvs() {
        organisationLines = readOrganisations();
        publicContentGroupLines = readPublicContentGroups();
        publicContentDateLines = readPublicContentDates();
        externalUserLines = readExternalUsers();
        internalUserLines = readInternalUsers();
        assessmentLines = readAssessments();
        assessorUserLines = readAssessorUsers();
        assessorResponseLines = readAssessorResponses();
        inviteLines = readInvites();
        applicationLines = readApplications();
        inviteLines = readInvites();
        questionResponseLines = readApplicationQuestionResponses();
        applicationFinanceLines = readApplicationFinances();
        competitionLines = buildCompetitionLines();
        questionnaireResponseLines = readQuestionnaireResponseLines();
    }

    @PostConstruct
    public void replaceExternalDependencies() {

        IdentityProviderService idpServiceMock = mock(IdentityProviderService.class);
        EmailService emailServiceMock = mock(EmailService.class);
        SilExperianEndpoint silExperianEndpointMock = mock(SilExperianEndpoint.class);
        SilCrmEndpoint silCrmEndpointMock = mock(SilCrmEndpoint.class);

        when(idpServiceMock.createUserRecordWithUid(isA(String.class), isA(String.class))).thenAnswer(
                user -> serviceSuccess(UUID.randomUUID().toString()));
        when(idpServiceMock.activateUser(isA(String.class))).thenAnswer(ServiceResult::serviceSuccess);
        when(idpServiceMock.deactivateUser(isA(String.class))).thenAnswer(ServiceResult::serviceSuccess);

        when(emailServiceMock.sendEmail(isA(EmailAddress.class), isA(List.class), isA(String.class), isA(String.class), isA(String.class))).
                thenReturn(serviceSuccess(emptyList()));

        when(silExperianEndpointMock.validate(isA(SILBankDetails.class))).thenReturn(serviceSuccess(new ValidationResult(true, "", emptyList())));
        when(silExperianEndpointMock.verify(isA(AccountDetails.class))).thenReturn(serviceSuccess(new VerificationResult("10", "10", "10", "10", emptyList())));

        when(silCrmEndpointMock.updateLoanApplicationState(any(SilLoanApplication.class))).thenReturn(serviceSuccess());
        when(silCrmEndpointMock.updateLoanAssessment(any(SilLoanAssessment.class))).thenReturn(serviceSuccess());

        RegistrationService registrationServiceUnwrapped = (RegistrationService) unwrapProxy(registrationService);
        ReflectionTestUtils.setField(registrationServiceUnwrapped, "idpService", idpServiceMock);

        BankDetailsService bankDetailsServiceUnwrapped = (BankDetailsService) unwrapProxy(bankDetailsService);
        ReflectionTestUtils.setField(bankDetailsServiceUnwrapped, "silExperianEndpoint", silExperianEndpointMock);

        CrmService crmServiceUnwrapped = (CrmService) unwrapProxy(crmService);
        ReflectionTestUtils.setField(crmServiceUnwrapped, "silCrmEndpoint", silCrmEndpointMock);
    }

    @After
    public void tearDownFiles() throws Exception {
        File f = new File(storageLocation);
        if (f.exists()) {
            FileSystemUtils.deleteRecursively(f);
        }
    }

    @Test
    public void generateTestData() {

        long before = System.currentTimeMillis();

        LOG.info("Starting generating data...");
        System.out.println("Starting generating data...");

        fixUpDatabase();

        createOrganisations();
        createInternalUsers();
        createExternalUsers();

        List<CompetitionLine> competitionsToProcess = simpleFilter(competitionLines, competitionFilter);

        List<CompletableFuture<CompetitionData>> createCompetitionFutures =
                createCompetitions(competitionsToProcess);

        List<CompletableFuture<List<ApplicationData>>> createApplicationsFutures =
                fillInAndCompleteApplications(createCompetitionFutures);

        CompletableFuture<Void> competitionFundersFutures = waitForFutureList(createCompetitionFutures).thenRunAsync(() ->
                createCompetitionFundersForCompetitions(createCompetitionFutures), taskExecutor);

        CompletableFuture<Void> competitionOrganisationConfigFutures = waitForFutureList(createCompetitionFutures).thenRunAsync(() ->
                createCompetitionOrganisationConfigForCompetitions(createCompetitionFutures), taskExecutor);

        CompletableFuture<Void> publicContentFutures = waitForFutureList(createCompetitionFutures).thenRunAsync(() ->
                createPublicContent(createCompetitionFutures), taskExecutor);

        CompletableFuture<Void> supporterFutures = waitForFutureList(createApplicationsFutures).thenRunAsync(() ->
                createSupporters(createCompetitionFutures, createApplicationsFutures), taskExecutor);

        CompletableFuture<Void> assessorFutures = waitForFutureList(createApplicationsFutures).thenRunAsync(() ->
                createAssessorsAndAssessments(createCompetitionFutures, createApplicationsFutures), taskExecutor);

        CompletableFuture<Void> competitionsFinalisedFuture = assessorFutures.thenRunAsync(() -> {

            List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
            List<ApplicationData> applications = flattenLists(simpleMap(createApplicationsFutures, CompletableFuture::join));

            createFundingDecisions(competitions);
            createProjects(applications);
            moveCompetitionsIntoFinalState(competitions);

        }, taskExecutor);

        CompletableFuture<Void> competitionAssessmentPeriodsFutures = waitForFutureList(createCompetitionFutures).thenRunAsync(() ->
                createAssessmentPeriodsForCompetitions(createCompetitionFutures), taskExecutor);

        CompletableFuture.allOf(competitionFundersFutures,
                                publicContentFutures,
                                assessorFutures,
                                competitionsFinalisedFuture,
                                competitionOrganisationConfigFutures,
                                supporterFutures,
                                competitionAssessmentPeriodsFutures
        ).join();

        UserResource user = userService.findByEmail("ifs_system_maintenance_user@innovateuk.org").getSuccess();
        setLoggedInUser(user);

        projectToBeCreatedService.createAllPendingProjects();

        long after = System.currentTimeMillis();

        LOG.info("Finished generating data in " + ((after - before) / 1000) + " seconds");
        System.out.println("Finished generating data in " + ((after - before) / 1000) + " seconds");
    }

    private void moveCompetitionsIntoFinalState(List<CompetitionData> competitions) {
        competitionDataBuilderService.moveCompetitionsToCorrectFinalState(competitions);
    }

    private void createProjects(List<ApplicationData> applications) {
        projectDataBuilderService.createProjects(applications);
    }

    private void createFundingDecisions(List<CompetitionData> competitions) {
        competitions.forEach(competition -> {

            CompetitionLine competitionLine = simpleFindFirstMandatory(competitionLines, l ->
                    Objects.equals(l.getName(), competition.getCompetition().getName()));

            applicationDataBuilderService.createFundingDecisions(competition, competitionLine, applicationLines);
        });
    }

    private void createAssessorsAndAssessments(List<CompletableFuture<CompetitionData>> createCompetitionFutures, List<CompletableFuture<List<ApplicationData>>> createApplicationsFutures) {
        List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
        List<ApplicationData> applications = flattenLists(simpleMap(createApplicationsFutures, CompletableFuture::join));

        List<String> competitionNames = simpleMap(competitions, c -> c.getCompetition().getName());
        List<String> applicationNames = simpleMap(applications, a -> a.getApplication().getName());

        List<AssessorUserLine> filteredAssessorLines = simpleFilter(this.assessorUserLines, l -> competitionNames.contains(l.competitionName));
        List<AssessmentLine> filteredAssessmentLines = simpleFilter(this.assessmentLines, l -> applicationNames.contains(l.applicationName));
        List<InviteLine> filteredAssessorInviteLines = simpleFilter(this.inviteLines, l -> "COMPETITION".equals(l.type) && competitionNames.contains(l.targetName));
        List<AssessorResponseLine> filteredAssessorResponseLines = simpleFilter(this.assessorResponseLines, l -> applicationNames.contains(l.applicationName));

        assessmentDataBuilderService.createAssessors(competitions, filteredAssessorLines, filteredAssessorInviteLines);
        assessmentDataBuilderService.createNonRegisteredAssessorInvites(competitions, filteredAssessorInviteLines);
        assessmentDataBuilderService.createAssessments(applications, filteredAssessmentLines, filteredAssessorResponseLines, this.competitionLines);

    }

    private void createSupporters(List<CompletableFuture<CompetitionData>> createCompetitionFutures, List<CompletableFuture<List<ApplicationData>>> createApplicationsFutures) {
        simpleMap(createCompetitionFutures, CompletableFuture::join);
        List<ApplicationData> applications = flattenLists(simpleMap(createApplicationsFutures, CompletableFuture::join));

        List<ApplicationResource> applicationsForCofunding = applications.stream()
                .filter(app -> app.getCompetition().getFundingType() == FundingType.KTP)
                .map(ApplicationData::getApplication)
                .collect(toList());

        List<ExternalUserLine> filteredSupporters = simpleFilter(this.externalUserLines, l -> l.role == Role.SUPPORTER);

        supporterDataService.buildSupporters(applicationsForCofunding, filteredSupporters);
    }


    private void createPublicContent(List<CompletableFuture<CompetitionData>> createCompetitionFutures) {
        List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
        createPublicContentGroups(competitions);
        createPublicContentDates(competitions);
    }

    private void createCompetitionFundersForCompetitions(List<CompletableFuture<CompetitionData>> createCompetitionFutures) {
        List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
        createCompetitionFunders(competitions);
    }

    private void createCompetitionOrganisationConfigForCompetitions(List<CompletableFuture<CompetitionData>> createCompetitionFutures) {
        List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
        createCompetitionOrganisationConfig(competitions);
    }

    private void createAssessmentPeriodsForCompetitions(List<CompletableFuture<CompetitionData>> createCompetitionFutures) {
        List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
        createCompetitionAssessmentPeriods(competitions);
    }

    private List<CompletableFuture<CompetitionData>> createCompetitions(List<CompetitionLine> competitionLines) {
        return simpleMap(competitionLines, line -> CompletableFuture.supplyAsync(() ->
                competitionDataBuilderService.createCompetition(line), taskExecutor));
    }

    private Function<ApplicationData, CompletableFuture<ApplicationData>> fillInAndCompleteApplicationFn = applicationData -> {

        ApplicationLine applicationLine = getApplicationLineForApplication(applicationData);

        CompletableFuture<List<ApplicationQuestionResponseData>> questionResponses = CompletableFuture.supplyAsync(() ->
                applicationDataBuilderService.createApplicationQuestionResponses(applicationData, applicationLine, questionResponseLines),
                taskExecutor);

        CompletableFuture<List<ApplicationFinanceData>> applicationFinances = CompletableFuture.supplyAsync(() ->
                applicationDataBuilderService.createApplicationFinances(applicationData, applicationLine, applicationFinanceLines, externalUserLines),
                taskExecutor);

        applicationFinances.join(); // wait for finances to be created.

        CompletableFuture<List<QuestionnaireResponseData>> questionnaireResponses = CompletableFuture.supplyAsync(() ->
                        applicationDataBuilderService.createQuestionnaireResponse(applicationData, applicationLine, questionnaireResponseLines, externalUserLines),
                taskExecutor);

        List<QuestionnaireResponseData> questionnaireResponseData = questionnaireResponses.join();

        CompletableFuture<List<SubsidyBasisData>> subsidyBasis = CompletableFuture.supplyAsync(() ->
                        applicationDataBuilderService.createSubsidyBasis(applicationLine, questionnaireResponseData),
                taskExecutor);

        CompletableFuture<List<ProcurementMilestoneData>> procurementMilestones = CompletableFuture.supplyAsync(() ->
                        applicationDataBuilderService.createProcurementMilestones(applicationData, applicationLine, externalUserLines),
                taskExecutor);



        CompletableFuture<Void> allQuestionsAnswered = CompletableFuture.allOf(questionResponses, applicationFinances, questionnaireResponses, subsidyBasis, procurementMilestones);

        return allQuestionsAnswered.thenApplyAsync(done -> {

            List<ApplicationQuestionResponseData> responses = questionResponses.join();
            List<ApplicationFinanceData> finances = applicationFinances.join();
            applicationDataBuilderService.completeApplication(applicationData, applicationLine, responses, finances);
            return applicationData;

        }, taskExecutor);
    };

    private ApplicationLine getApplicationLineForApplication(ApplicationData applicationData) {
        return simpleFindFirstMandatory(applicationLines, l ->
                        l.title.equals(applicationData.getApplication().getName()));
    }

    private Function<CompetitionData, List<CompletableFuture<ApplicationData>>> fillInAndCompleteApplications = competitionData -> {

        List<CompletableFuture<ApplicationData>> applicationFutures = createBasicApplicationDetails(competitionData);

        return simpleMap(applicationFutures, applicationFuture ->
                applicationFuture.thenComposeAsync(fillInAndCompleteApplicationFn, taskExecutor));
    };

    public List<CompletableFuture<List<ApplicationData>>> fillInAndCompleteApplications(List<CompletableFuture<CompetitionData>> createCompetitionFutures) {

        return simpleMap(createCompetitionFutures, competition -> {

            CompletableFuture<List<CompletableFuture<ApplicationData>>> competitionAndApplicationFutures =
                    competition.thenApplyAsync(fillInAndCompleteApplications, taskExecutor);

            return competitionAndApplicationFutures.thenApply(applicationFutures ->
                    simpleMap(applicationFutures, CompletableFuture::join));
        });
    }

    public List<CompletableFuture<ApplicationData>> createBasicApplicationDetails(CompetitionData competition) {

        List<CsvUtils.ApplicationLine> applicationsForCompetition = simpleFilter(applicationLines, applicationLine ->
                applicationLine.competitionName.equals(competition.getCompetition().getName()));

        if (applicationsForCompetition.isEmpty()) {
            return emptyList();
        }

        competitionDataBuilderService.moveCompetitionIntoOpenStatus(competition);

        return simpleMap(applicationsForCompetition, applicationLine -> CompletableFuture.supplyAsync(() ->
                applicationDataBuilderService.createApplication(competition, applicationLine, inviteLines, externalUserLines), taskExecutor));
    }

    private CompletableFuture<Void> waitForFutureList(List<? extends CompletableFuture<?>> createApplicationsFutures) {
        return CompletableFuture.allOf(createApplicationsFutures.toArray(new CompletableFuture[] {}));
    }

    private void createExternalUsers() {
        externalUserLines.forEach(userDataBuilderService::createExternalUser);
    }

    private void createCompetitionFunders(List<CompetitionData> competitions) {
        competitions.forEach(competitionDataBuilderService::createCompetitionFunder);
    }

    private void createCompetitionOrganisationConfig(List<CompetitionData> competitions) {
        competitions.forEach(competitionDataBuilderService::createCompetitionOrganisationConfig);
    }

    private void createCompetitionAssessmentPeriods(List<CompetitionData> competitions) {
        competitions.forEach(competitionDataBuilderService::createCompetitionAssessmentPeriods);
    }

    private void createPublicContentGroups(List<CompetitionData> competitions) {

        testService.doWithinTransaction(this::setDefaultCompAdmin);

        competitions.forEach(competition -> {

            List<PublicContentGroupLine> publicContentLine = simpleFilter(publicContentGroupLines, l ->
                    Objects.equals(competition.getCompetition().getName(), l.competitionName));

            publicContentLine.forEach(competitionDataBuilderService::createPublicContentGroup);
        });
    }

    private void createPublicContentDates(List<CompetitionData> competitions) {

        testService.doWithinTransaction(this::setDefaultCompAdmin);

        competitions.forEach(competition -> {

            List<PublicContentDateLine> publicContentLines = simpleFilter(publicContentDateLines, l ->
                    Objects.equals(competition.getCompetition().getName(), l.competitionName));

            publicContentLines.forEach(competitionDataBuilderService::createPublicContentDate);
        });
    }

    private void createOrganisations() {

        List<CompletableFuture<Void>> futures = simpleMap(organisationLines, line -> CompletableFuture.runAsync(() ->
                organisationDataBuilderService.createOrganisation(line), taskExecutor));

        waitForFutureList(futures).join();
    }

    private void createInternalUsers() {
        internalUserLines.forEach(userDataBuilderService::createInternalUser);
    }

    private void freshDb() throws Exception {
        try {
            cleanAndMigrateDatabaseWithPatches(locations.split(","));
        } catch (Exception e) {
            fail("Exception thrown migrating with script directories: " + Arrays.toString(locations.split(",")) + e.getMessage());
        }
    }

    private Object unwrapProxy(Object services) {
        try {
            return unwrapProxies(singletonList(services)).get(0);
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
        Flyway flyway = Flyway.configure()
                .dataSource(databaseUrl, databaseUser, databasePassword)
                .locations(patchLocations)
                .placeholders(placeholders).load();
        flyway.clean();
        flyway.migrate();
    }

    protected abstract boolean cleanDbFirst();

    protected abstract void fixUpDatabase();

    private void setDefaultCompAdmin() {
        setLoggedInUser(newUserResource().withRolesGlobal(asList(Role.SYSTEM_REGISTRATION_USER)).build());
        testService.doWithinTransaction(() ->
                setLoggedInUser(userService.findByEmail(COMP_ADMIN_EMAIL).getSuccess())
        );
    }
}
