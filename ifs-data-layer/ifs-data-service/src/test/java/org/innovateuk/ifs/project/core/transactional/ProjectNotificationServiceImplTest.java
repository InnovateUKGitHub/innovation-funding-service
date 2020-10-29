package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProjectNotificationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @InjectMocks
    private ProjectNotificationService service = new ProjectNotificationServiceImpl();

    private static final String WEB_BASE_URL = "www.baseUrl.com" ;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        ReflectionTestUtils.setField(service, "webBaseUrl", WEB_BASE_URL);
    }

    @Test
    public void sendProjectSetupNotification() {
        User leadUser = newUser().withEmailAddress("leadapplicant@example.com").build();
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(Role.LEADAPPLICANT).build();

        User ktaUser = newUser().withEmailAddress("kta@example.com").build();
        ProcessRole ktaProcessRole = newProcessRole().withUser(ktaUser).withRole(Role.KNOWLEDGE_TRANSFER_ADVISER).build();

        Competition competition = newCompetition().withFundingType(FundingType.LOAN).build();
        Application application = newApplication()
                .withProcessRoles(leadProcessRole, ktaProcessRole)
                .withCompetition(competition).build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(notificationService.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = service.sendProjectSetupNotification(application.getId());

        verify(notificationService).sendNotificationWithFlush(createLambdaMatcher(notification -> {
            assertEquals(application.getId(), notification.getGlobalArguments().get("competitionNumber"));
            assertEquals(competition.getName(), notification.getGlobalArguments().get("competitionName"));
            assertEquals(2, notification.getTo().size());
            assertEquals(leadUser.getEmail(), notification.getTo().get(0).getEmailAddress());
            assertEquals(leadUser.getName(), notification.getTo().get(0).getName());
            assertEquals(ktaUser.getEmail(), notification.getTo().get(1).getEmailAddress());
            assertEquals(ktaUser.getName(), notification.getTo().get(1).getName());
        }), eq(EMAIL));
        assertTrue(result.isSuccess());
    }

    @Test
    public void sendProjectSetupNotification_For_NotFound_Application() {
        User leadUser = newUser().withEmailAddress("leadapplicant@example.com").build();
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(Role.LEADAPPLICANT).build();

        User ktaUser = newUser().withEmailAddress("kta@example.com").build();
        ProcessRole ktaProcessRole = newProcessRole().withUser(ktaUser).withRole(Role.KNOWLEDGE_TRANSFER_ADVISER).build();

        Competition competition = newCompetition().withFundingType(FundingType.LOAN).build();
        Application application = newApplication()
                .withProcessRoles(leadProcessRole, ktaProcessRole)
                .withCompetition(competition).build();


        when(applicationRepository.findById(application.getId())).thenReturn(Optional.empty());

        ServiceResult<Void> result = service.sendProjectSetupNotification(application.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void sendProjectSetupNotification_For_Inactive_Application_User() {
        User leadUser = newUser().withStatus(UserStatus.INACTIVE).build();
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(Role.LEADAPPLICANT).build();

        User ktaUser = newUser().withEmailAddress("kta@example.com").build();
        ProcessRole ktaProcessRole = newProcessRole().withUser(ktaUser).withRole(Role.KNOWLEDGE_TRANSFER_ADVISER).build();

        Competition competition = newCompetition().withFundingType(FundingType.LOAN).build();
        Application application = newApplication()
                .withProcessRoles(leadProcessRole, ktaProcessRole)
                .withCompetition(competition).build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(notificationService.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = service.sendProjectSetupNotification(application.getId());

        verify(notificationService).sendNotificationWithFlush(createLambdaMatcher(notification -> {
            assertEquals(1, notification.getTo().size());
            assertEquals(ktaUser.getEmail(), notification.getTo().get(0).getEmailAddress());
            assertEquals(ktaUser.getName(), notification.getTo().get(0).getName());
        }), eq(EMAIL));
        assertTrue(result.isSuccess());
    }
}