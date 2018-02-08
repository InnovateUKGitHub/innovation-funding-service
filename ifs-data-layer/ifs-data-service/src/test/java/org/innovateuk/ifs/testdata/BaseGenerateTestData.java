package org.innovateuk.ifs.testdata;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
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
import org.innovateuk.ifs.invite.constant.InviteStatus;
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
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.UnaryOperator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.testdata.BaseDataBuilderService.COMP_ADMIN_EMAIL;
import static org.innovateuk.ifs.testdata.BaseDataBuilderService.PROJECT_FINANCE_EMAIL;
import static org.innovateuk.ifs.testdata.CsvUtils.*;
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
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
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
    private NotificationSender emailNotificationSender;

    @Autowired
    private BankDetailsService bankDetailsService;

    @Autowired
    protected CompetitionRepository competitionRepository;

    @Autowired
    private TestService testService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private CompetitionDataBuilderService competitionDataBuilderService;

    @Autowired
    private ApplicationDataBuilderService applicationDataBuilderService;

    private CompetitionDataBuilder competitionDataBuilder;
    private QuestionDataBuilder questionDataBuilder;
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

    private static List<OrganisationLine> organisationLines;

    private static List<CompetitionLine> competitionLines;

    private static List<QuestionLine> questionLines;

    private static List<CompetitionFunderLine> competitionFunderLines;

    private static List<PublicContentGroupLine> publicContentGroupLines;

    private static List<PublicContentDateLine> publicContentDateLines;

    private static List<ApplicationLine> applicationLines;

    private static List<InviteLine> inviteLines;

    private static List<ExternalUserLine> externalUserLines;

    private static List<AssessorUserLine> assessorUserLines;

    private static List<InternalUserLine> internalUserLines;

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
        questionDataBuilder = newQuestionData(serviceLocator);
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

        List<CompletableFuture<CompetitionData>> createCompetitionFutures =
                competitionDataBuilderService.createCompetitions();

        List<CompletableFuture<List<ApplicationData>>> createApplicationsFutures =
                applicationDataBuilderService.fillInAndCompleteApplication(createCompetitionFutures);

        waitForFuturesToComplete(createApplicationsFutures);

        competitionDataBuilderService.moveCompetitionsToCorrectFinalState();

        Future<?> fundingDecisions = taskExecutor.submit(() -> createFundingDecisions(competitionLines));

        taskExecutor.submit(this::updateQuestions);
        taskExecutor.submit(this::createCompetitionFunders);
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

    private void updateQuestions() {
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
        externalUserLines.forEach(line -> createUser(externalUserBuilder, line));
    }

    private void createAssessors() {
        assessorUserLines.forEach(this::createAssessor);
    }

    private void createNonRegisteredAssessorInvites() {
        List<InviteLine> assessorInvites = simpleFilter(inviteLines, invite -> "COMPETITION".equals(invite.type));
        List<InviteLine> nonRegisteredAssessorInvites = simpleFilter(assessorInvites, invite -> !userRepository.findByEmail(invite.email).isPresent());
        nonRegisteredAssessorInvites.forEach(line -> createAssessorInvite(assessorInviteUserBuilder, line));
    }

    private void createAssessments() {
        assessmentLines.forEach(this::createAssessment);
        assessorResponseLines.forEach(this::createAssessorResponse);
        assessmentLines.forEach(this::submitAssessment);
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
        competitionFunderLines.forEach(this::createCompetitionFunder);
    }

    private void createPublicContentGroups() {
        testService.doWithinTransaction(this::setDefaultCompAdmin);
        publicContentGroupLines.forEach(this::createPublicContentGroup);
    }

    private void createPublicContentDates() {
        publicContentDateLines.forEach(this::createPublicContentDate);
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

        internalUserLines.forEach(line -> {

            testService.doWithinTransaction(() -> {

                setDefaultSystemRegistrar();

                List<UserRoleType> roles = simpleMap(line.roles, UserRoleType::fromName);

                InternalUserDataBuilder baseBuilder = internalUserBuilder.withRoles(roles);

                createUser(baseBuilder, line);
            });
        });
    }

    private void createFundingDecisions(List<CsvUtils.CompetitionLine> competitionLines) {

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

    private List<Pair<String, FundingDecision>> createFundingDecisionsFromCsv(String competitionName) {
        List<CsvUtils.ApplicationLine> matchingApplications = simpleFilter(applicationLines, a -> a.competitionName.equals(competitionName));
        List<CsvUtils.ApplicationLine> applicationsWithDecisions = simpleFilter(matchingApplications, a -> asList(ApplicationState.APPROVED, ApplicationState.REJECTED).contains(a.status));
        return simpleMap(applicationsWithDecisions, ma -> Pair.of(ma.title, ma.status == ApplicationState.APPROVED ? FundingDecision.FUNDED : FundingDecision.UNFUNDED));
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
                setLoggedInUser(userService.findByEmail(BaseDataBuilder.IFS_SYSTEM_REGISTRAR_USER_EMAIL).getSuccessObjectOrThrowException())
        );
    }

    private void setDefaultCompAdmin() {
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
}
