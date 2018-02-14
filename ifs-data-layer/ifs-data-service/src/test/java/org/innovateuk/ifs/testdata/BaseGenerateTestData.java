package org.innovateuk.ifs.testdata;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.flywaydb.core.Flyway;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.service.EmailService;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.notifications.service.senders.email.EmailNotificationSender;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.sil.experian.resource.AccountDetails;
import org.innovateuk.ifs.sil.experian.resource.SILBankDetails;
import org.innovateuk.ifs.sil.experian.resource.ValidationResult;
import org.innovateuk.ifs.sil.experian.resource.VerificationResult;
import org.innovateuk.ifs.sil.experian.service.SilExperianEndpoint;
import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.testdata.builders.data.ApplicationData;
import org.innovateuk.ifs.testdata.builders.data.BaseUserData;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.testdata.services.*;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.testdata.builders.CompetitionFunderDataBuilder.newCompetitionFunderData;
import static org.innovateuk.ifs.testdata.builders.ExternalUserDataBuilder.newExternalUserData;
import static org.innovateuk.ifs.testdata.builders.InternalUserDataBuilder.newInternalUserData;
import static org.innovateuk.ifs.testdata.builders.OrganisationDataBuilder.newOrganisationData;
import static org.innovateuk.ifs.testdata.builders.PublicContentDateDataBuilder.newPublicContentDateDataBuilder;
import static org.innovateuk.ifs.testdata.builders.PublicContentGroupDataBuilder.newPublicContentGroupDataBuilder;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.COMP_ADMIN_EMAIL;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.PROJECT_FINANCE_EMAIL;
import static org.innovateuk.ifs.testdata.services.CsvUtils.*;
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
@SpringBootTest(classes = GenerateTestDataConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
abstract class BaseGenerateTestData extends BaseIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(BaseGenerateTestData.class);

    @SuppressWarnings("unused")
    private static final Predicate<CompetitionLine> ALL_COMPETITIONS_PREDICATE =
            competitionLine -> true;

    @SuppressWarnings("unused")
    private static final Predicate<CompetitionLine> SPECIFIC_COMPETITIONS_PREDICATE =
            competitionLine -> "Expression of Interest: Quantum Computing algorithms for combating antibiotic resistance through simulation".equals(competitionLine.name);

    private static final Predicate<CompetitionLine> COMPETITIONS_FILTER =
            ALL_COMPETITIONS_PREDICATE;

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
    protected OrganisationRepository organisationRepository;

    @Autowired
    private NotificationSender emailNotificationSender;

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

    private CompetitionDataBuilder competitionDataBuilder;
    private CompetitionFunderDataBuilder competitionFunderDataBuilder;
    private PublicContentGroupDataBuilder publicContentGroupDataBuilder;
    private PublicContentDateDataBuilder publicContentDateDataBuilder;
    private ExternalUserDataBuilder externalUserBuilder;
    private InternalUserDataBuilder internalUserBuilder;
    private OrganisationDataBuilder organisationBuilder;

    private static List<OrganisationLine> organisationLines;
    private static List<CompetitionLine> competitionLines;
    private static List<CompetitionFunderLine> competitionFunderLines;
    private static List<PublicContentGroupLine> publicContentGroupLines;
    private static List<PublicContentDateLine> publicContentDateLines;
    private static List<ApplicationLine> applicationLines;
    private static List<ExternalUserLine> externalUserLines;
    private static List<InternalUserLine> internalUserLines;

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
        competitionFunderLines = readCompetitionFunders();
        publicContentGroupLines = readPublicContentGroups();
        publicContentDateLines = readPublicContentDates();
        applicationLines = readApplications();
        externalUserLines = readExternalUsers();
        internalUserLines = readInternalUsers();
    }

    @PostConstruct
    public void replaceExternalDependencies() {

        IdentityProviderService idpServiceMock = mock(IdentityProviderService.class);
        EmailService emailServiceMock = mock(EmailService.class);
        SilExperianEndpoint silExperianEndpointMock = mock(SilExperianEndpoint.class);

        when(idpServiceMock.createUserRecordWithUid(isA(String.class), isA(String.class))).thenAnswer(
                user -> serviceSuccess(UUID.randomUUID().toString()));
        when(idpServiceMock.activateUser(isA(String.class))).thenAnswer(ServiceResult::serviceSuccess);
        when(idpServiceMock.deactivateUser(isA(String.class))).thenAnswer(ServiceResult::serviceSuccess);

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

        competitionDataBuilder = newCompetitionData(serviceLocator);
        competitionFunderDataBuilder = newCompetitionFunderData(serviceLocator);
        externalUserBuilder = newExternalUserData(serviceLocator);
        internalUserBuilder = newInternalUserData(serviceLocator);
        organisationBuilder = newOrganisationData(serviceLocator);
        publicContentGroupDataBuilder = newPublicContentGroupDataBuilder(serviceLocator);
        publicContentDateDataBuilder = newPublicContentDateDataBuilder(serviceLocator);
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

        List<CompetitionLine> competitionsToProcess = simpleFilter(BaseGenerateTestData.competitionLines, COMPETITIONS_FILTER);

        List<CompletableFuture<CompetitionData>> createCompetitionFutures =
                competitionDataBuilderService.createCompetitions(competitionsToProcess);

        List<CompletableFuture<List<ApplicationData>>> createApplicationsFutures =
                applicationDataBuilderService.fillInAndCompleteApplications(createCompetitionFutures);

        CompletableFuture<Void> competitionFundersFutures = waitForFutureList(createCompetitionFutures).thenRunAsync(() -> {
            List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
            createCompetitionFunders(competitions);
        }, taskExecutor);

        CompletableFuture<Void> publicContentFutures = waitForFutureList(createCompetitionFutures).thenRunAsync(() -> {
            List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
            createPublicContentGroups(competitions);
            createPublicContentDates(competitions);
        }, taskExecutor);

        CompletableFuture<Void> assessorFutures = waitForFutureList(createApplicationsFutures).thenRunAsync(() -> {

            List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
            List<ApplicationData> applications = flattenLists(simpleMap(createApplicationsFutures, CompletableFuture::join));

            assessmentDataBuilderService.createAssessors(competitions);
            assessmentDataBuilderService.createNonRegisteredAssessorInvites(competitions);
            assessmentDataBuilderService.createAssessments(applications);
        }, taskExecutor);

        CompletableFuture<Void> competitionsFinalisedFuture = assessorFutures.thenRunAsync(() -> {

            List<CompetitionData> competitions = simpleMap(createCompetitionFutures, CompletableFuture::join);
            List<ApplicationData> applications = flattenLists(simpleMap(createApplicationsFutures, CompletableFuture::join));

            createFundingDecisions(competitions);
            projectDataBuilderService.createProjects(applications);
            competitionDataBuilderService.moveCompetitionsToCorrectFinalState(competitions);
        }, taskExecutor);

        CompletableFuture.allOf(competitionFundersFutures, publicContentFutures, assessorFutures, competitionsFinalisedFuture).join();

        long after = System.currentTimeMillis();

        LOG.info("Finished generating data in " + ((after - before) / 1000) + " seconds");
        System.out.println("Finished generating data in " + ((after - before) / 1000) + " seconds");
    }

    private CompletableFuture<Void> waitForFutureList(List<? extends CompletableFuture<?>> createApplicationsFutures) {
        return CompletableFuture.allOf(createApplicationsFutures.toArray(new CompletableFuture[] {}));
    }

    private void createExternalUsers() {
        externalUserLines.forEach(line -> createUser(externalUserBuilder, line));
    }

    private void createCompetitionFunders(List<CompetitionData> competitions) {
        competitions.forEach(this::createCompetitionFunder);
    }

    private void createPublicContentGroups(List<CompetitionData> competitions) {

        testService.doWithinTransaction(this::setDefaultCompAdmin);

        competitions.forEach(competition -> {

            Optional<PublicContentGroupLine> publicContentLine = simpleFindFirst(publicContentGroupLines, l ->
                    Objects.equals(competition.getCompetition().getName(), l.competitionName));

            publicContentLine.ifPresent(this::createPublicContentGroup);
        });
    }

    private void createPublicContentDates(List<CompetitionData> competitions) {

        testService.doWithinTransaction(this::setDefaultCompAdmin);

        competitions.forEach(competition -> {

            Optional<PublicContentDateLine> publicContentLine = simpleFindFirst(publicContentDateLines, l ->
                    Objects.equals(competition.getCompetition().getName(), l.competitionName));

            publicContentLine.ifPresent(this::createPublicContentDate);
        });
    }

    private void createCompetitionFunder(CompetitionData competition) {

        Optional<CompetitionFunderLine> funderLine = simpleFindFirst(competitionFunderLines, l ->
                Objects.equals(competition.getCompetition().getName(), l.competitionName));

        funderLine.ifPresent(line ->
            competitionFunderDataBuilder.
                    withCompetitionFunderData(line.competitionName, line.funder, line.funder_budget, line.co_funder).
                    build());
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

        List<CompletableFuture<Void>> futures = simpleMap(organisationLines, line -> CompletableFuture.runAsync(() -> {

            OrganisationDataBuilder organisation =
                    organisationBuilder.createOrganisation(line.name, line.companyRegistrationNumber, lookupOrganisationType(line.organisationType));

            for (OrganisationAddressType organisationType : line.addressType) {
                organisation = organisation.withAddress(organisationType,
                        line.addressLine1, line.addressLine2,
                        line.addressLine3, line.town,
                        line.postcode, line.county);
            }

            organisation.build();
        }, taskExecutor));

        waitForFutureList(futures).join();
    }

    private void createInternalUsers() {

        internalUserLines.forEach(line -> testService.doWithinTransaction(() -> {

            setDefaultSystemRegistrar();

            List<UserRoleType> roles = simpleMap(line.roles, UserRoleType::fromName);

            InternalUserDataBuilder baseBuilder = internalUserBuilder.withRoles(roles);

            createUser(baseBuilder, line);
        }));
    }

    private void createFundingDecisions(List<CompetitionData> competitions) {

        competitions.forEach(competition -> {

            CompetitionLine competitionLine = simpleFindFirstMandatory(competitionLines, l ->
                    Objects.equals(l.name, competition.getCompetition().getName()));

            CompetitionDataBuilder basicCompetitionInformation = competitionDataBuilder.withExistingCompetition(competition);

            if (competitionLine.fundersPanelEndDate != null && competitionLine.fundersPanelEndDate.isBefore(ZonedDateTime.now())) {

                basicCompetitionInformation.
                        moveCompetitionIntoFundersPanelStatus().
                        sendFundingDecisions(createFundingDecisionsFromCsv(competitionLine.name)).
                        build();
            }
        });
    }

    private List<Pair<String, FundingDecision>> createFundingDecisionsFromCsv(String competitionName) {
        List<CsvUtils.ApplicationLine> matchingApplications = simpleFilter(applicationLines, a -> a.competitionName.equals(competitionName));
        List<CsvUtils.ApplicationLine> applicationsWithDecisions = simpleFilter(matchingApplications, a -> asList(ApplicationState.APPROVED, ApplicationState.REJECTED).contains(a.status));
        return simpleMap(applicationsWithDecisions, ma -> Pair.of(ma.title, ma.status == ApplicationState.APPROVED ? FundingDecision.FUNDED : FundingDecision.UNFUNDED));
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
        Flyway f = new Flyway();
        f.setDataSource(databaseUrl, databaseUser, databasePassword);
        f.setLocations(patchLocations);
        f.setPlaceholders(placeholders);
        f.clean();
        f.migrate();
    }

    private <T extends BaseUserData, S extends BaseUserDataBuilder<T, S>> void createUser(S baseBuilder, UserLine line) {

        UnaryOperator<S> registerUserIfNecessary = builder -> builder.registerUser(line.firstName, line.lastName, line.emailAddress, line.organisationName, line.phoneNumber);

        UnaryOperator<S> verifyEmail = BaseUserDataBuilder::verifyEmail;

        UnaryOperator<S> inactivateUserIfNecessary = builder -> !(line.emailVerified) ? builder.deactivateUser() : builder;

        registerUserIfNecessary.andThen(verifyEmail).andThen(inactivateUserIfNecessary).apply(baseBuilder).build();
    }

    private OrganisationTypeEnum lookupOrganisationType(String organisationType) {
        return OrganisationTypeEnum.valueOf(organisationType.toUpperCase().replace(" ", "_"));
    }

    protected abstract boolean cleanDbFirst();

    protected abstract void fixUpDatabase();

    private void setDefaultSystemRegistrar() {
        setLoggedInUser(newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build());
        testService.doWithinTransaction(() ->
                setLoggedInUser(userService.findByEmail(BaseDataBuilder.IFS_SYSTEM_REGISTRAR_USER_EMAIL).getSuccess())
        );
    }

    private void setDefaultCompAdmin() {
        setLoggedInUser(newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build());
        testService.doWithinTransaction(() ->
                setLoggedInUser(userService.findByEmail(COMP_ADMIN_EMAIL).getSuccess())
        );
    }
}
