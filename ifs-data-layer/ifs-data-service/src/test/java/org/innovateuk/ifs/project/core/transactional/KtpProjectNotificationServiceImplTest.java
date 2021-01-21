package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class KtpProjectNotificationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @InjectMocks
    private KtpProjectNotificationService service = new KtpProjectNotificationServiceImpl();

    private static final String WEB_BASE_URL = "www.baseUrl.com" ;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        ReflectionTestUtils.setField(service, "webBaseUrl", WEB_BASE_URL);
    }

    @Test
    public void sendProjectSetupNotification() {
        User leadUser = newUser().withEmailAddress("leadapplicant@example.com").build();
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(ProcessRoleType.LEADAPPLICANT).build();

        User collaborator = newUser().withEmailAddress("collaborator@example.com").build();
        ProcessRole collaboratorProcessRole = newProcessRole().withUser(collaborator).withRole(ProcessRoleType.LEADAPPLICANT).build();

        User ktaUser = newUser().withEmailAddress("kta@example.com").build();
        ProcessRole ktaProcessRole = newProcessRole().withUser(ktaUser).withRole(ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER).build();

        Competition competition = newCompetition().withFundingType(FundingType.KTP).build();
        Application application = newApplication()
                .withProcessRoles(leadProcessRole, collaboratorProcessRole, ktaProcessRole)
                .withCompetition(competition).build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(notificationService.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = service.sendProjectSetupNotification(application.getId());

        verify(notificationService).sendNotificationWithFlush(createLambdaMatcher(notification -> {
            assertEquals(competition.getId(), notification.getGlobalArguments().get("competitionNumber"));
            assertEquals(competition.getName(), notification.getGlobalArguments().get("competitionName"));
            assertEquals(3, notification.getTo().size());
            assertThat(notification.getTo(), containsInAnyOrder(
                    allOf(hasProperty("to",
                            equalTo(new UserNotificationTarget(leadUser.getName(), leadUser.getEmail())))),
                    allOf(hasProperty("to",
                            equalTo(new UserNotificationTarget(collaborator.getName(), collaborator.getEmail())))),
                    allOf(hasProperty("to",
                            equalTo(new UserNotificationTarget(ktaUser.getName(), ktaUser.getEmail()))))
                    )
            );
        }), eq(EMAIL));
        assertTrue(result.isSuccess());
    }

    @Test
    public void sendProjectSetupNotification_For_NotFound_Application() {
        User leadUser = newUser().withEmailAddress("leadapplicant@example.com").build();
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(ProcessRoleType.LEADAPPLICANT).build();

        User ktaUser = newUser().withEmailAddress("kta@example.com").build();
        ProcessRole ktaProcessRole = newProcessRole().withUser(ktaUser).withRole(ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER).build();

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
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(ProcessRoleType.LEADAPPLICANT).build();

        User ktaUser = newUser().withEmailAddress("kta@example.com").build();
        ProcessRole ktaProcessRole = newProcessRole().withUser(ktaUser).withRole(ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER).build();

        Competition competition = newCompetition().withFundingType(FundingType.LOAN).build();
        Application application = newApplication()
                .withProcessRoles(leadProcessRole, ktaProcessRole)
                .withCompetition(competition).build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(notificationService.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = service.sendProjectSetupNotification(application.getId());

        verify(notificationService).sendNotificationWithFlush(createLambdaMatcher(notification -> {
            assertEquals(1, notification.getTo().size());
            assertEquals(ktaUser.getEmail(), notification.getTo().get(0).getTo().getEmailAddress());
            assertEquals(ktaUser.getName(), notification.getTo().get(0).getTo().getName());
        }), eq(EMAIL));
        assertTrue(result.isSuccess());
    }
}