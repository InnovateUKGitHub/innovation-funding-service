package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.innovateuk.ifs.application.transactional.ApplicationNotificationServiceImpl.Notifications.*;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.APPLICATION_MUST_BE_INELIGIBLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationNotificationServiceImplTest {
    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Mock
    private ApplicationWorkflowHandler applicationWorkflowHandlerMock;

    @InjectMocks
    private ApplicationNotificationService service = new ApplicationNotificationServiceImpl();

    private static final String WEB_BASE_URL = "www.baseUrl.com" ;
    private static final String EARLY_METRICS_URL = "www.early-metrics.com" ;
    private static final Set<ApplicationState> FUNDING_DECISIONS_MADE_STATUSES = asLinkedSet(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED);

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        ReflectionTestUtils.setField(service, "webBaseUrl", WEB_BASE_URL);
        ReflectionTestUtils.setField(service, "earlyMetricsUrl", EARLY_METRICS_URL);
    }

    @Test
    public void sendNotificationApplicationSubmitted() {
        User leadUser = newUser().withEmailAddress("leadapplicant@example.com").build();
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(Role.LEADAPPLICANT).build();
        Competition competition = newCompetition().build();
        Application application = newApplication().withProcessRoles(leadProcessRole).withCompetition(competition).build();
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(notificationServiceMock.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = service.sendNotificationApplicationSubmitted(application.getId());

        verify(notificationServiceMock).sendNotificationWithFlush(createLambdaMatcher(notification -> {
            assertEquals(application.getName(), notification.getGlobalArguments().get("applicationName"));
            assertEquals(competition.getName(), notification.getGlobalArguments().get("competitionName"));
            assertEquals(WEB_BASE_URL, notification.getGlobalArguments().get("webBaseUrl"));
            assertEquals(1, notification.getTo().size());
            assertEquals(leadUser.getEmail(), notification.getTo().get(0).getEmailAddress());
            assertEquals(leadUser.getName(), notification.getTo().get(0).getName());
            assertEquals(APPLICATION_SUBMITTED, notification.getMessageKey());
        }), eq(EMAIL));
        assertTrue(result.isSuccess());
    }

    @Test
    public void sendNotificationApplicationSubmittedLoans() {
        User leadUser = newUser().withEmailAddress("leadapplicant@example.com").build();
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(Role.LEADAPPLICANT).build();
        Competition competition = newCompetition().withFundingType(FundingType.LOAN).build();
        Application application = newApplication().withProcessRoles(leadProcessRole).withCompetition(competition).build();
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(notificationServiceMock.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = service.sendNotificationApplicationSubmitted(application.getId());

        verify(notificationServiceMock).sendNotificationWithFlush(createLambdaMatcher(notification -> {
            assertEquals(application.getName(), notification.getGlobalArguments().get("applicationName"));
            assertEquals(competition.getName(), notification.getGlobalArguments().get("competitionName"));
            assertEquals(competition.submissionDateDisplay(), notification.getGlobalArguments().get("compCloseDate"));
            assertEquals(EARLY_METRICS_URL, notification.getGlobalArguments().get("earlyMetricsUrl"));
            assertEquals(1, notification.getTo().size());
            assertEquals(leadUser.getEmail(), notification.getTo().get(0).getEmailAddress());
            assertEquals(leadUser.getName(), notification.getTo().get(0).getName());
            assertEquals(LOANS_APPLICATION_SUBMITTED, notification.getMessageKey());
        }), eq(EMAIL));
        assertTrue(result.isSuccess());
    }

    @Test
    public void sendNotificationApplicationSubmitted_horizon2020() {
        User leadUser = newUser().withEmailAddress("leadapplicant@example.com").build();
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(Role.LEADAPPLICANT).build();
        Competition competition = newCompetition().withCompetitionType(newCompetitionType().withName(CompetitionResource.H2020_TYPE_NAME).build()).build();
        Application application = newApplication().withProcessRoles(leadProcessRole).withCompetition(competition).build();
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(notificationServiceMock.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = service.sendNotificationApplicationSubmitted(application.getId());

        verify(notificationServiceMock).sendNotificationWithFlush(createLambdaMatcher(notification -> {
            assertEquals(application.getName(), notification.getGlobalArguments().get("applicationName"));
            assertEquals(1, notification.getTo().size());
            assertEquals(leadUser.getEmail(), notification.getTo().get(0).getEmailAddress());
            assertEquals(leadUser.getName(), notification.getTo().get(0).getName());
            assertEquals(HORIZON_2020_APPLICATION_SUBMITTED, notification.getMessageKey());
        }), eq(EMAIL));
        assertTrue(result.isSuccess());
    }

    @Test
    public void notifyApplicantsByCompetition() {
        Long competitionId = 1L;
        Long applicationOneId = 2L;
        Long applicationTwoId = 3L;
        Long applicationThreeId = 4L;

        Competition competition = newCompetition()
                .withName("Competition")
                .build();

        List<User> users = newUser()
                .withFirstName("John", "Jane", "Bob")
                .withLastName("Smith", "Jones", "Davies")
                .withEmailAddress("john@smith.com", "jane@jones.com", "bob@davie.com")
                .build(3);

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withId(applicationOneId, applicationTwoId, applicationThreeId)
                .withName("App1", "App2", "App3")
                .build(3);

        List<ProcessRole> processRoles = newProcessRole()
                .withUser(users.get(0), users.get(1), users.get(2))
                .withRole(Role.LEADAPPLICANT)
                .withApplication(applications.toArray(new Application[0]))
                .build(3);

        applications.get(0).setProcessRoles(singletonList(processRoles.get(0)));
        applications.get(1).setProcessRoles(singletonList(processRoles.get(1)));
        applications.get(2).setProcessRoles(singletonList(processRoles.get(2)));

        List<NotificationTarget> notificationTargets = asList(
                new UserNotificationTarget(users.get(0).getName(), users.get(0).getEmail()),
                new UserNotificationTarget(users.get(1).getName(), users.get(1).getEmail()),
                new UserNotificationTarget(users.get(2).getName(), users.get(2).getEmail())
        );

        List<Notification> notifications = asList(
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(0)),
                        ApplicationNotificationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(0).getName(),
                                "applicationName", applications.get(0).getName(),
                                "applicationId", applications.get(0).getId(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL)
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(1)),
                        ApplicationNotificationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(1).getName(),
                                "applicationName", applications.get(1).getName(),
                                "applicationId", applications.get(1).getId(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL)
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(2)),
                        ApplicationNotificationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(2).getName(),
                                "applicationName", applications.get(2).getName(),
                                "applicationId", applications.get(2).getId(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL)
                )
        );

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES)).thenReturn(applications);

        when(applicationRepositoryMock.findById(applicationOneId)).thenReturn(Optional.of(applications.get(0)));
        when(applicationRepositoryMock.findById(applicationTwoId)).thenReturn(Optional.of(applications.get(1)));
        when(applicationRepositoryMock.findById(applicationThreeId)).thenReturn(Optional.of(applications.get(2)));

        notifications.forEach(notification ->
                when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess())
        );

        ServiceResult<Void> result = service.notifyApplicantsByCompetition(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, notificationServiceMock);
        inOrder.verify(applicationRepositoryMock).findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES);

        inOrder.verify(applicationRepositoryMock).findById(applicationOneId);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(notifications.get(0), EMAIL);

        inOrder.verify(applicationRepositoryMock).findById(applicationTwoId);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(notifications.get(1), EMAIL);

        inOrder.verify(applicationRepositoryMock).findById(applicationThreeId);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(notifications.get(2), EMAIL);

        inOrder.verifyNoMoreInteractions();

        assertTrue(result.isSuccess());
    }

    @Test
    public void notifyApplicantsByCompetition_oneFailure() {
        Long competitionId = 1L;
        Long applicationOneId = 2L;
        Long applicationTwoId = 3L;
        Long applicationThreeId = 4L;

        Competition competition = newCompetition()
                .withName("Competition")
                .build();

        List<User> users = newUser()
                .withFirstName("John", "Jane", "Bob")
                .withLastName("Smith", "Jones", "Davies")
                .withEmailAddress("john@smith.com", "jane@jones.com", "bob@davie.com")
                .build(3);


        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withId(applicationOneId, applicationTwoId, applicationThreeId)
                .withName("App1", "App2", "App3")
                .build(3);

        List<ProcessRole> processRoles = newProcessRole()
                .withUser(users.get(0), users.get(1), users.get(2))
                .withRole(Role.LEADAPPLICANT)
                .withApplication(applications.toArray(new Application[0]))
                .build(3);

        applications.get(0).setProcessRoles(singletonList(processRoles.get(0)));
        applications.get(1).setProcessRoles(singletonList(processRoles.get(1)));
        applications.get(2).setProcessRoles(singletonList(processRoles.get(2)));

        List<NotificationTarget> notificationTargets = asList(
                new UserNotificationTarget(users.get(0).getName(), users.get(0).getEmail()),
                new UserNotificationTarget(users.get(1).getName(), users.get(1).getEmail()),
                new UserNotificationTarget(users.get(2).getName(), users.get(2).getEmail())
        );

        List<EmailContent> emailContents = newEmailContentResource()
                .build(3);

        List<Notification> notifications = asList(
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(0)),
                        ApplicationNotificationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(0).getName(),
                                "applicationName", applications.get(0).getName(),
                                "applicationId", applications.get(0).getId(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL)
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(1)),
                        ApplicationNotificationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(1).getName(),
                                "applicationName", applications.get(1).getName(),
                                "applicationId", applications.get(1).getId(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL)
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(2)),
                        ApplicationNotificationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(2).getName(),
                                "applicationName", applications.get(2).getName(),
                                "applicationId", applications.get(2).getId(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL)
                )
        );

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES)).thenReturn(applications);

        when(applicationRepositoryMock.findById(applicationOneId)).thenReturn(Optional.of(applications.get(0)));
        when(applicationRepositoryMock.findById(applicationTwoId)).thenReturn(Optional.of(applications.get(1)));
        when(applicationRepositoryMock.findById(applicationThreeId)).thenReturn(Optional.of(applications.get(2)));

        when(notificationServiceMock.sendNotificationWithFlush(notifications.get(0), EMAIL)).thenReturn(serviceSuccess());
        when(notificationServiceMock.sendNotificationWithFlush(notifications.get(1), EMAIL)).thenReturn(serviceSuccess());
        when(notificationServiceMock.sendNotificationWithFlush(notifications.get(2), EMAIL)).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<Void> result = service.notifyApplicantsByCompetition(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, notificationServiceMock);
        inOrder.verify(applicationRepositoryMock).findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES);

        inOrder.verify(applicationRepositoryMock).findById(applicationOneId);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(notifications.get(0), EMAIL);

        inOrder.verify(applicationRepositoryMock).findById(applicationTwoId);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(notifications.get(1), EMAIL);

        inOrder.verify(applicationRepositoryMock).findById(applicationThreeId);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(notifications.get(2), EMAIL);

        inOrder.verifyNoMoreInteractions();

        assertTrue(result.isFailure());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void notifyApplicantsByCompetition_allFailure() {
        Long competitionId = 1L;
        Long applicationOneId = 2L;
        Long applicationTwoId = 3L;
        Long applicationThreeId = 4L;

        Competition competition = newCompetition()
                .withName("Competition")
                .build();

        List<User> users = newUser()
                .withFirstName("John", "Jane", "Bob")
                .withLastName("Smith", "Jones", "Davies")
                .withEmailAddress("john@smith.com", "jane@jones.com", "bob@davie.com")
                .build(3);

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withId(applicationOneId, applicationTwoId, applicationThreeId)
                .withName("App1", "App2", "App3")
                .build(3);

        List<ProcessRole> processRoles = newProcessRole()
                .withUser(users.get(0), users.get(1), users.get(2))
                .withRole(Role.LEADAPPLICANT)
                .withApplication(applications.toArray(new Application[0]))
                .build(3);

        applications.get(0).setProcessRoles(singletonList(processRoles.get(0)));
        applications.get(1).setProcessRoles(singletonList(processRoles.get(1)));
        applications.get(2).setProcessRoles(singletonList(processRoles.get(2)));

        List<NotificationTarget> notificationTargets = asList(
                new UserNotificationTarget(users.get(0).getName(), users.get(0).getEmail()),
                new UserNotificationTarget(users.get(1).getName(), users.get(1).getEmail()),
                new UserNotificationTarget(users.get(2).getName(), users.get(2).getEmail())
        );

        List<EmailContent> emailContents = newEmailContentResource()
                .build(3);

        List<Notification> notifications = asList(
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(0)),
                        ApplicationNotificationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(0).getName(),
                                "applicationName", applications.get(0).getName(),
                                "applicationId", applications.get(0).getId(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL)
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(1)),
                        ApplicationNotificationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(1).getName(),
                                "applicationName", applications.get(1).getName(),
                                "applicationId", applications.get(1).getId(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL)
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(2)),
                        ApplicationNotificationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(2).getName(),
                                "applicationName", applications.get(2).getName(),
                                "applicationId", applications.get(2).getId(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL)
                )
        );

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES)).thenReturn(applications);

        when(applicationRepositoryMock.findById(applicationOneId)).thenReturn(Optional.of(applications.get(0)));
        when(applicationRepositoryMock.findById(applicationTwoId)).thenReturn(Optional.of(applications.get(1)));
        when(applicationRepositoryMock.findById(applicationThreeId)).thenReturn(Optional.of(applications.get(2)));

        notifications.forEach(notification ->
                when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceFailure(internalServerErrorError()))
        );

        ServiceResult<Void> result = service.notifyApplicantsByCompetition(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, notificationServiceMock);
        inOrder.verify(applicationRepositoryMock).findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES);

        inOrder.verify(applicationRepositoryMock).findById(applicationOneId);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(notifications.get(0), EMAIL);

        inOrder.verifyNoMoreInteractions();

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void informIneligible() {
        long applicationId = 1L;
        String subject = "subject";
        String message = "message";
        String email = "email@address.com";
        String firstName = "first";
        String lastName = "last";
        String fullName = String.format("%s %s", firstName, lastName);

        ApplicationIneligibleSendResource resource = newApplicationIneligibleSendResource()
                .withSubject(subject)
                .withMessage(message)
                .build();

        User[] users = newUser()
                .withFirstName(firstName, "other")
                .withLastName(lastName, "other")
                .withEmailAddress(email, "other@email.com")
                .buildArray(2, User.class);

        ProcessRole[] processRoles = newProcessRole()
                .withUser(users)
                .withRole(Role.LEADAPPLICANT, Role.COLLABORATOR)
                .buildArray(2, ProcessRole.class);

        Competition competition = newCompetition()
                .withName("Competition")
                .build();

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(competition)
                .withProcessRoles(processRoles)
                .build();

        Map<String, Object> expectedNotificationArguments = asMap(
                "subject", subject,
                "bodyPlain", message,
                "bodyHtml", message,
                "competitionName", competition.getName(),
                "applicationId", application.getId(),
                "applicationName", application.getName()
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to = new UserNotificationTarget(fullName, email);
        Notification notification = new Notification(
                from,
                singletonList(to),
                ApplicationNotificationServiceImpl.Notifications
                        .APPLICATION_INELIGIBLE,
                expectedNotificationArguments
        );

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationWorkflowHandlerMock.informIneligible(application)).thenReturn(true);
        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> serviceResult = service.informIneligible(applicationId, resource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(applicationRepositoryMock, applicationWorkflowHandlerMock, notificationServiceMock);
        inOrder.verify(applicationRepositoryMock).findById(applicationId);
        inOrder.verify(applicationWorkflowHandlerMock).informIneligible(application);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void informIneligible_workflowError() {
        long applicationId = 1L;
        String subject = "subject";
        String message = "message";

        ApplicationIneligibleSendResource resource = newApplicationIneligibleSendResource()
                .withSubject(subject)
                .withMessage(message)
                .build();

        Application application = newApplication()
                .withId(applicationId)
                .build();

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationWorkflowHandlerMock.informIneligible(application)).thenReturn(false);

        ServiceResult<Void> serviceResult = service.informIneligible(applicationId, resource);
        assertTrue(serviceResult.isFailure());
        assertEquals(APPLICATION_MUST_BE_INELIGIBLE.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());

        InOrder inOrder = inOrder(applicationRepositoryMock, applicationWorkflowHandlerMock, notificationServiceMock);
        inOrder.verify(applicationRepositoryMock).findById(applicationId);
        inOrder.verify(applicationWorkflowHandlerMock).informIneligible(application);
        inOrder.verifyNoMoreInteractions();
    }
}