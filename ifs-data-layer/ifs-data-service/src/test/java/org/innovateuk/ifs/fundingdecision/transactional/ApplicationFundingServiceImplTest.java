package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.fundingdecision.mapper.FundingDecisionMapper;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.text.Collator;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.resource.FundingDecision.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigBuilder.newCompetitionAssessmentConfig;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingServiceImpl.Notifications.APPLICATION_FUNDING;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ApplicationFundingServiceImplTest extends BaseServiceUnitTest<ApplicationFundingService> {

    private static final String webBaseUrl = "http://ifs-local-dev";

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private AverageAssessorScoreRepository averageAssessorScoreRepository;

    @Mock
    private FundingDecisionMapper fundingDecisionMapper;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private ApplicationWorkflowHandler applicationWorkflowHandler;

    @Mock
    private AssessorFormInputResponseService assessorFormInputResponseService;

    private Competition competition;
    
    @Override
    protected ApplicationFundingService supplyServiceUnderTest() {
        ApplicationFundingServiceImpl service = new ApplicationFundingServiceImpl();
        ReflectionTestUtils.setField(service, "webBaseUrl", webBaseUrl);
        return service;
    }

    @Before
    public void setup() {
    	competition = newCompetition().withAssessorFeedbackDate("01/02/2017 17:30:00").withCompetitionStatus(CompetitionStatus.FUNDERS_PANEL).withCompetitionAssessmentConfig(newCompetitionAssessmentConfig().withIncludeAverageAssessorScoreInNotifications(true).build()).withId(123L).build();
    	when(competitionRepository.findById(123L)).thenReturn(Optional.of(competition));
    	
    	when(fundingDecisionMapper.mapToDomain(any(FundingDecision.class))).thenAnswer(new Answer<FundingDecisionStatus>(){
			@Override
			public FundingDecisionStatus answer(InvocationOnMock invocation) throws Throwable {
				return FundingDecisionStatus.valueOf(((FundingDecision)invocation.getArguments()[0]).name());
			}});
    	when(fundingDecisionMapper.mapToResource(any(FundingDecisionStatus.class))).thenAnswer(new Answer<FundingDecision>(){
			@Override
			public FundingDecision answer(InvocationOnMock invocation) throws Throwable {
				return FundingDecision.valueOf(((FundingDecisionStatus)invocation.getArguments()[0]).name());
			}});
    }

    @Test
    public void testNotifyLeadApplicantsOfFundingDecisions() {
        CompetitionAssessmentConfig competitionAssessmentConfig = new CompetitionAssessmentConfig();

        Competition competition = newCompetition()
                .withCompetitionAssessmentConfig(competitionAssessmentConfig)
                .build();

        Application application1 = newApplication().withCompetition(competition).build();
        Application application2 = newApplication().withCompetition(competition).build();
        Application application3 = newApplication().withCompetition(competition).build();

        User application1LeadApplicant = newUser().build();
        User application2LeadApplicant = newUser().build();
        User application3LeadApplicant = newUser().build();

        List<ProcessRole> leadApplicantProcessRoles = newProcessRole().
                withUser(application1LeadApplicant, application2LeadApplicant, application3LeadApplicant).
                withApplication(application1, application2, application3).
                withRole(Role.LEADAPPLICANT).
                build(3);

        UserNotificationTarget application1LeadApplicantTarget = new UserNotificationTarget(application1LeadApplicant.getName(), application1LeadApplicant.getEmail());
        UserNotificationTarget application2LeadApplicantTarget = new UserNotificationTarget(application2LeadApplicant.getName(), application2LeadApplicant.getEmail());
        UserNotificationTarget application3LeadApplicantTarget = new UserNotificationTarget(application3LeadApplicant.getName(), application3LeadApplicant.getEmail());
        List<NotificationTarget> expectedLeadApplicants = asList(application1LeadApplicantTarget, application2LeadApplicantTarget, application3LeadApplicantTarget);

        Map<Long, FundingDecision> decisions = MapFunctions.asMap(
                application1.getId(), FundingDecision.FUNDED,
                application2.getId(), FundingDecision.UNFUNDED,
                application3.getId(), FundingDecision.ON_HOLD);

        FundingNotificationResource fundingNotificationResource = new FundingNotificationResource("The message body.", decisions);

        Map<String, Object> expectedGlobalNotificationArguments = asMap("message", fundingNotificationResource.getMessageBody());

        Map<NotificationTarget, Map<String, Object>> expectedTargetSpecificNotificationArguments = asMap(
                application1LeadApplicantTarget, asMap(
                        "applicationName", application1.getName(),
                        "competitionName", application1.getCompetition().getName(),
                        "applicationId", application1.getId()),

                application2LeadApplicantTarget, asMap(
                        "applicationName", application2.getName(),
                        "competitionName", application2.getCompetition().getName(),
                        "applicationId", application2.getId()),

                application3LeadApplicantTarget, asMap(
                        "applicationName", application3.getName(),
                        "competitionName", application3.getCompetition().getName(),
                        "applicationId", application3.getId()));

        Notification expectedFundingNotification = new Notification(systemNotificationSource, expectedLeadApplicants, APPLICATION_FUNDING, expectedGlobalNotificationArguments, expectedTargetSpecificNotificationArguments);

        List<Long> applicationIds = asList(application1.getId(), application2.getId(), application3.getId());
        List<Application> applications = asList(application1, application2, application3);
        when(applicationRepository.findAllById(applicationIds)).thenReturn(applications);

        leadApplicantProcessRoles.forEach(processRole ->
                when(processRoleRepository.findByApplicationIdAndRole(processRole.getApplicationId(), processRole.getRole())).thenReturn(singletonList(processRole))
        );

        when(notificationService.sendNotificationWithFlush(createNotificationExpectationsWithGlobalArgs(expectedFundingNotification), eq(EMAIL))).thenReturn(serviceSuccess());
        when(applicationService.setApplicationFundingEmailDateTime(any(Long.class), any(ZonedDateTime.class))).thenReturn(serviceSuccess(new ApplicationResource()));

        when(competitionService.manageInformState(competition.getId())).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.notifyApplicantsOfFundingDecisions(fundingNotificationResource);
        assertTrue(result.isSuccess());

        verify(notificationService).sendNotificationWithFlush(createNotificationExpectationsWithGlobalArgs(expectedFundingNotification), eq(EMAIL));
        verifyNoMoreInteractions(notificationService);

        verify(applicationService).setApplicationFundingEmailDateTime(eq(application1.getId()), any(ZonedDateTime.class));
        verify(applicationService).setApplicationFundingEmailDateTime(eq(application2.getId()), any(ZonedDateTime.class));
        verify(applicationService).setApplicationFundingEmailDateTime(eq(application3.getId()), any(ZonedDateTime.class));
        verifyNoMoreInteractions(applicationService);
    }

    @Test
    public void testNotifyLeadApplicantsOfFundingDecisionsWithAverageAssessorScore() {
        CompetitionAssessmentConfig competitionAssessmentConfig = new CompetitionAssessmentConfig();
        competitionAssessmentConfig.setIncludeAverageAssessorScoreInNotifications(true);

        Competition competition = newCompetition().withCompetitionAssessmentConfig(competitionAssessmentConfig).build();

        Application application1 = newApplication().withCompetition(competition).build();
        Application application2 = newApplication().withCompetition(competition).build();
        Application application3 = newApplication().withCompetition(competition).build();

        User application1LeadApplicant = newUser().build();
        User application2LeadApplicant = newUser().build();
        User application3LeadApplicant = newUser().build();

        List<ProcessRole> leadApplicantProcessRoles = newProcessRole().
                withUser(application1LeadApplicant, application2LeadApplicant, application3LeadApplicant).
                withApplication(application1, application2, application3).
                withRole(Role.LEADAPPLICANT).
                build(3);

        UserNotificationTarget application1LeadApplicantTarget = new UserNotificationTarget(application1LeadApplicant.getName(), application1LeadApplicant.getEmail());
        UserNotificationTarget application2LeadApplicantTarget = new UserNotificationTarget(application2LeadApplicant.getName(), application2LeadApplicant.getEmail());
        UserNotificationTarget application3LeadApplicantTarget = new UserNotificationTarget(application3LeadApplicant.getName(), application3LeadApplicant.getEmail());
        List<NotificationTarget> expectedLeadApplicants = asList(application1LeadApplicantTarget, application2LeadApplicantTarget, application3LeadApplicantTarget);

        AverageAssessorScore averageAssessorScore1 = new AverageAssessorScore(application1, BigDecimal.valueOf(90));
        AverageAssessorScore averageAssessorScore2 = new AverageAssessorScore(application2, BigDecimal.valueOf(60));
        AverageAssessorScore averageAssessorScore3 = new AverageAssessorScore(application3, BigDecimal.valueOf(70));

        Map<Long, FundingDecision> decisions = MapFunctions.asMap(
                application1.getId(), FundingDecision.FUNDED,
                application2.getId(), FundingDecision.UNFUNDED,
                application3.getId(), FundingDecision.ON_HOLD);

        FundingNotificationResource fundingNotificationResource = new FundingNotificationResource("The message body.", decisions);

        Map<String, Object> expectedGlobalNotificationArguments = asMap("message", fundingNotificationResource.getMessageBody());

        Map<NotificationTarget, Map<String, Object>> expectedTargetSpecificNotificationArguments = asMap(
                application1LeadApplicantTarget, asMap(
                        "applicationName", application1.getName(),
                        "competitionName", application1.getCompetition().getName(),
                        "applicationId", application1.getId(),
                        "averageAssessorScore", "Average assessor score: " + averageAssessorScore1.getScore() + "%"),

                application2LeadApplicantTarget, asMap(
                        "applicationName", application2.getName(),
                        "competitionName", application2.getCompetition().getName(),
                        "applicationId", application2.getId(),
                        "averageAssessorScore", "Average assessor score: " + averageAssessorScore2.getScore() + "%"),

                application3LeadApplicantTarget, asMap(
                        "applicationName", application3.getName(),
                        "competitionName", application3.getCompetition().getName(),
                        "applicationId", application3.getId(),
                        "averageAssessorScore", "Average assessor score: " + averageAssessorScore3.getScore() + "%"));

        Notification expectedFundingNotification = new Notification(systemNotificationSource, expectedLeadApplicants, APPLICATION_FUNDING, expectedGlobalNotificationArguments, expectedTargetSpecificNotificationArguments);

        List<Long> applicationIds = asList(application1.getId(), application2.getId(), application3.getId());
        List<Application> applications = asList(application1, application2, application3);
        when(applicationRepository.findAllById(applicationIds)).thenReturn(applications);

        leadApplicantProcessRoles.forEach(processRole ->
                when(processRoleRepository.findByApplicationIdAndRole(processRole.getApplicationId(), processRole.getRole())).thenReturn(singletonList(processRole))
        );

        when(averageAssessorScoreRepository.findByApplicationId(application1.getId())).thenReturn(Optional.of(averageAssessorScore1));
        when(averageAssessorScoreRepository.findByApplicationId(application2.getId())).thenReturn(Optional.of(averageAssessorScore2));
        when(averageAssessorScoreRepository.findByApplicationId(application3.getId())).thenReturn(Optional.of(averageAssessorScore3));
        when(notificationService.sendNotificationWithFlush(createNotificationExpectationsWithGlobalArgs(expectedFundingNotification), eq(EMAIL))).thenReturn(serviceSuccess());
        when(applicationService.setApplicationFundingEmailDateTime(any(Long.class), any(ZonedDateTime.class))).thenReturn(serviceSuccess(new ApplicationResource()));

        when(competitionService.manageInformState(competition.getId())).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.notifyApplicantsOfFundingDecisions(fundingNotificationResource);
        assertTrue(result.isSuccess());

        verify(notificationService).sendNotificationWithFlush(createNotificationExpectationsWithGlobalArgs(expectedFundingNotification), eq(EMAIL));
        verifyNoMoreInteractions(notificationService);

        verify(applicationService).setApplicationFundingEmailDateTime(eq(application1.getId()), any(ZonedDateTime.class));
        verify(applicationService).setApplicationFundingEmailDateTime(eq(application2.getId()), any(ZonedDateTime.class));
        verify(applicationService).setApplicationFundingEmailDateTime(eq(application3.getId()), any(ZonedDateTime.class));
        verifyNoMoreInteractions(applicationService);
    }

    @Test
    public void testNotifyAllApplicantsOfFundingDecisions() {
        CompetitionAssessmentConfig competitionAssessmentConfig = new CompetitionAssessmentConfig();
        Competition competition = newCompetition().withCompetitionAssessmentConfig(competitionAssessmentConfig).build();

        Application application1 = newApplication().withCompetition(competition).build();
        Application application2 = newApplication().withCompetition(competition).build();

        // add some collaborators into the mix - they should receive Notifications, and applicants who should not
        User application1LeadApplicant = newUser().build();
        User application1Collaborator = newUser().build();
        User application1Applicant = newUser().build();
        User application2LeadApplicant = newUser().build();
        User application2Collaborator = newUser().build();
        User application2Applicant = newUser().build();


        List<ProcessRole> allProcessRoles = newProcessRole().
                withUser(application1LeadApplicant, application1Collaborator, application1Applicant, application2LeadApplicant, application2Collaborator, application2Applicant).
                withApplication(application1, application1, application1, application2, application2, application2).
                withRole(Role.LEADAPPLICANT, Role.COLLABORATOR, Role.APPLICANT, Role.LEADAPPLICANT, Role.COLLABORATOR, Role.APPLICANT).
                build(6);

        UserNotificationTarget application1LeadApplicantTarget = new UserNotificationTarget(application1LeadApplicant.getName(), application1LeadApplicant.getEmail());
        UserNotificationTarget application2LeadApplicantTarget = new UserNotificationTarget(application2LeadApplicant.getName(), application2LeadApplicant.getEmail());
        UserNotificationTarget application1CollaboratorTarget = new UserNotificationTarget(application1Collaborator.getName(), application1Collaborator.getEmail());
        UserNotificationTarget application2CollaboratorTarget = new UserNotificationTarget(application2Collaborator.getName(), application2Collaborator.getEmail());
        List<NotificationTarget> expectedApplicants = asList(application1LeadApplicantTarget, application2LeadApplicantTarget, application1CollaboratorTarget, application2CollaboratorTarget);

        Map<Long, FundingDecision> decisions = MapFunctions.asMap(
                application1.getId(), FundingDecision.FUNDED,
                application2.getId(), FundingDecision.UNFUNDED);
        FundingNotificationResource fundingNotificationResource = new FundingNotificationResource("The message body.", decisions);

        Notification expectedFundingNotification =
                new Notification(systemNotificationSource, expectedApplicants, APPLICATION_FUNDING, emptyMap());
        
        List<Long> applicationIds = asList(application1.getId(), application2.getId());
        List<Application> applications = asList(application1, application2);
        when(applicationRepository.findAllById(applicationIds)).thenReturn(applications);

        asList(application1, application2).forEach(application ->
                when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application))
        );

        allProcessRoles.forEach(processRole ->
                when(processRoleRepository.findByApplicationIdAndRole(processRole.getApplicationId(), processRole.getRole())).thenReturn(singletonList(processRole))
        );

        when(notificationService.sendNotificationWithFlush(createSimpleNotificationExpectations(expectedFundingNotification), eq(EMAIL))).thenReturn(serviceSuccess());
        when(applicationService.setApplicationFundingEmailDateTime(any(Long.class), any(ZonedDateTime.class))).thenReturn(serviceSuccess(new ApplicationResource()));
        when(competitionService.manageInformState(competition.getId())).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.notifyApplicantsOfFundingDecisions(fundingNotificationResource);
        assertTrue(result.isSuccess());

        verify(notificationService).sendNotificationWithFlush(createSimpleNotificationExpectations(expectedFundingNotification), eq(EMAIL));
        verifyNoMoreInteractions(notificationService);

        verify(applicationService).setApplicationFundingEmailDateTime(eq(application1.getId()), any(ZonedDateTime.class));
        verify(applicationService).setApplicationFundingEmailDateTime(eq(application2.getId()), any(ZonedDateTime.class));
        verifyNoMoreInteractions(applicationService);
    }
    
    @Test
    public void testSaveFundingDecisionData() {
    	
    	Application application1 = newApplication().withId(1L).withCompetition(competition).withFundingDecision(FundingDecisionStatus.FUNDED).withApplicationState(ApplicationState.OPENED).build();
     	Application application2 = newApplication().withId(2L).withCompetition(competition).withFundingDecision(FundingDecisionStatus.UNFUNDED).withApplicationState(ApplicationState.OPENED).build();
    	when(applicationRepository.findByCompetitionId(competition.getId())).thenReturn(asList(application1, application2));

    	Map<Long, FundingDecision> decision = asMap(1L, UNDECIDED);
    	
    	ServiceResult<Void> result = service.saveFundingDecisionData(competition.getId(), decision);
    	
    	assertTrue(result.isSuccess());
    	verify(applicationRepository).findByCompetitionId(competition.getId());
    	assertEquals(ApplicationState.OPENED, application1.getApplicationProcess().getProcessState());
    	assertEquals(ApplicationState.OPENED, application2.getApplicationProcess().getProcessState());
    	assertEquals(FundingDecisionStatus.UNDECIDED, application1.getFundingDecision());
    	assertEquals(FundingDecisionStatus.UNFUNDED, application2.getFundingDecision());
    	assertNull(competition.getFundersPanelEndDate());
    }

    @Test
    public void testSaveFundingDecisionDataWillResetEmailDate() {

        Long applicationId = 1L;
        Long competitionId = competition.getId();
        Application application1 = newApplication().withId(applicationId).withCompetition(competition).withFundingDecision(FundingDecisionStatus.FUNDED).withApplicationState(ApplicationState.OPENED).build();
        when(applicationRepository.findByCompetitionId(competitionId)).thenReturn(singletonList(application1));

        Map<Long, FundingDecision> applicationDecisions = asMap(applicationId, UNDECIDED);

        ServiceResult<Void> result = service.saveFundingDecisionData(competitionId, applicationDecisions);

        assertTrue(result.isSuccess());
        verify(applicationRepository).findByCompetitionId(competitionId);
        verify(applicationService).setApplicationFundingEmailDateTime(applicationId, null);
    }

    @Test
    public void testSaveFundingDecisionDataWhenDecisionIsChanged() {
        Long applicationId = 1L;
        Long competitionId = competition.getId();
        Application application1 = newApplication()
                .withId(applicationId)
                .withCompetition(competition)
                .withFundingDecision(FundingDecisionStatus.UNDECIDED)
                .withApplicationState(ApplicationState.OPENED)
                .build();

        when(applicationRepository.findByCompetitionId(competitionId))
                .thenReturn(singletonList(application1));

        Map<Long, FundingDecision> applicationDecisions = asMap(applicationId, FUNDED);
        ServiceResult<Void> result = service.saveFundingDecisionData(competitionId, applicationDecisions);

        Map<Long, FundingDecision> changedApplicationDecisions = asMap(applicationId, UNFUNDED);
        ServiceResult<Void> changedResult = service.saveFundingDecisionData(competitionId, changedApplicationDecisions);

        assertTrue(result.isSuccess());
        assertTrue(changedResult.isSuccess());
        verify(applicationRepository, times(2)).findByCompetitionId(competitionId);
        verify(applicationService, times(2)).setApplicationFundingEmailDateTime(applicationId, null);
        verifyZeroInteractions(applicationWorkflowHandler);

        assertTrue(FundingDecisionStatus.UNFUNDED.equals(application1.getFundingDecision()));
    }

    @Test
    public void testSaveFundingDecisionDataWontResetEmailDateForSameDecision() {
        Long applicationId = 1L;
        Long competitionId = competition.getId();
        Application application1 = newApplication().withId(applicationId).withCompetition(competition).withFundingDecision(FundingDecisionStatus.FUNDED).withApplicationState(ApplicationState.OPENED).build();
        when(applicationRepository.findByCompetitionId(competitionId)).thenReturn(singletonList(application1));

        Map<Long, FundingDecision> applicationDecisions = asMap(applicationId, FUNDED);

        ServiceResult<Void> result = service.saveFundingDecisionData(competitionId, applicationDecisions);

        assertTrue(result.isSuccess());
        verify(applicationRepository).findByCompetitionId(competitionId);
        verify(applicationService, never()).setApplicationFundingEmailDateTime(any(Long.class), any(ZonedDateTime.class));
    }

    @Test
    public void testSaveFundingDecisionDataForCompetitionInProjectSetup() {
        Long unsuccessfulApplicationId = 246L;
        Long projectSetupCompetitionId = 456L;

        CompetitionType competitionType = newCompetitionType()
                .withName("Sector")
                .build();

        Competition projectSetupCompetition = newCompetition()
                .withCompetitionStatus(CompetitionStatus.PROJECT_SETUP)
                .withCompetitionType(competitionType)
                .withId(projectSetupCompetitionId)
                .build();

        projectSetupCompetition.setReleaseFeedbackDate(ZonedDateTime.now().minusDays(2L));
        when(competitionRepository.findById(projectSetupCompetitionId)).thenReturn(Optional.of(projectSetupCompetition));

        Application unsuccessfulApplication = newApplication()
                .withId(unsuccessfulApplicationId)
                .withCompetition(projectSetupCompetition)
                .withFundingDecision(FundingDecisionStatus.UNFUNDED)
                .withApplicationState(ApplicationState.SUBMITTED)
                .build();

        assertTrue(projectSetupCompetition.getCompetitionStatus().equals(CompetitionStatus.PROJECT_SETUP));

        when(applicationRepository.findByCompetitionId(projectSetupCompetitionId)).thenReturn(singletonList(unsuccessfulApplication));
        when(applicationWorkflowHandler.approve(unsuccessfulApplication)).thenReturn(true);

        Map<Long, FundingDecision> applicationDecision = asMap(unsuccessfulApplicationId, FUNDED);

        ServiceResult<Void> result = service.saveFundingDecisionData(projectSetupCompetitionId, applicationDecision);

        assertTrue(result.isSuccess());
        verify(applicationRepository).findByCompetitionId(projectSetupCompetitionId);
        verify(applicationService).setApplicationFundingEmailDateTime(unsuccessfulApplicationId, null);
        verify(applicationWorkflowHandler).approve(any(Application.class));
        assertTrue(FundingDecisionStatus.FUNDED.equals(unsuccessfulApplication.getFundingDecision()));

    }

    public static Notification createNotificationExpectationsWithGlobalArgs(Notification expectedNotification) {

        return createLambdaMatcher(notification -> {
            assertEquals(expectedNotification.getFrom(), notification.getFrom());

            List<String> expectedToEmailAddresses = simpleMap(expectedNotification.getTo(), NotificationTarget::getEmailAddress);
            List<String> actualToEmailAddresses = simpleMap(notification.getTo(), NotificationTarget::getEmailAddress);
            assertEquals(expectedToEmailAddresses, actualToEmailAddresses);

            assertEquals(expectedNotification.getMessageKey(), notification.getMessageKey());
            assertEquals(expectedNotification.getGlobalArguments(), notification.getGlobalArguments());

            Map<NotificationTarget, Map<String, Object>> expectedTargetSpecifics = expectedNotification.getPerNotificationTargetArguments();
            Map<NotificationTarget, Map<String, Object>> actualTargetSpecifics = notification.getPerNotificationTargetArguments();

            assertEquals(expectedTargetSpecifics.size(), actualTargetSpecifics.size());

            expectedTargetSpecifics.forEach((target, expectedArguments) -> {
                Map<String, Object> actualArguments = actualTargetSpecifics.get(target);
                assertEquals(expectedArguments, actualArguments);
            });

            assertEquals(expectedTargetSpecifics, actualTargetSpecifics);
        });
    }

    public static Notification createSimpleNotificationExpectations(Notification expectedNotification) {

        return createLambdaMatcher(notification -> {
            assertEquals(expectedNotification.getFrom(), notification.getFrom());

            Collection<String> expectedTo = new TreeSet<>(Collator.getInstance());
            expectedTo.addAll(simpleMap(expectedNotification.getTo(), NotificationTarget::getEmailAddress));

            Collection<String> actualTo = new TreeSet<>(Collator.getInstance());
            actualTo.addAll(simpleMap(notification.getTo(), NotificationTarget::getEmailAddress));
            assertEquals(asList(expectedTo.toArray()), asList(actualTo.toArray()));

            assertEquals(expectedNotification.getMessageKey(), notification.getMessageKey());
        });
    }
}
