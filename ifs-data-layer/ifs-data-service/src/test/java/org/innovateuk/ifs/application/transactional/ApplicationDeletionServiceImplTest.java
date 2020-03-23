package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationUserCompositeId;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class ApplicationDeletionServiceImplTest extends BaseServiceUnitTest<ApplicationDeletionServiceImpl> {

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private FormInputResponseRepository formInputResponseRepository;

    @Mock
    private QuestionStatusRepository questionStatusRepository;

    @Mock
    private ProcessHistoryRepository processHistoryRepository;

    @Mock
    private DeletedApplicationRepository deletedApplicationRepository;

    @Mock
    private ApplicationHiddenFromDashboardRepository applicationHiddenFromDashboardRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Override
    protected ApplicationDeletionServiceImpl supplyServiceUnderTest() {
        return new ApplicationDeletionServiceImpl();
    }

    @Test
    public void deleteApplication() {
        long applicationId = 1L;
        Application application = newApplication()
                .withApplicationState(ApplicationState.OPENED)
                .build();
        String email = "test@test.com";
        String firstName = "test";
        String lastName = "test";
        User user = newUser()
                .withEmailAddress(email)
                .withFirstName(firstName)
                .withLastName(lastName)
                .build();
        ProcessRole processRole = newProcessRole()
                .withApplication(application)
                .withUser(user)
                .withRole(Role.LEADAPPLICANT)
                .build();
        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationName", application.getName());
        notificationArguments.put("leadUserName", user.getName());
        notificationArguments.put("leadEmail", user.getEmail());
        NotificationTarget to = new UserNotificationTarget(format("%s %s", firstName, lastName), email);
        Notification notification = new Notification(systemNotificationSource,
                emptyList(),
                ApplicationDeletionServiceImpl.Notifications.APPLICATION_DELETED,
                notificationArguments);

        setField(application.getApplicationProcess(), "id", 1L);
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(processRoleRepository.findByApplicationId(applicationId)).thenReturn(singletonList(processRole));
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.deleteApplication(applicationId);

        assertTrue(result.isSuccess());

        verify(applicationFinanceRepository).deleteByApplicationId(applicationId);
        verify(processRoleRepository).deleteByApplicationId(applicationId);
        verify(formInputResponseRepository).deleteByApplicationId(applicationId);
        verify(questionStatusRepository).deleteByApplicationId(applicationId);
        verify(applicationHiddenFromDashboardRepository).deleteByApplicationId(applicationId);
        verify(processHistoryRepository).deleteByProcessId(application.getApplicationProcess().getId());
        verify(applicationRepository).delete(application);
        verify(notificationService).sendNotificationWithFlush(notification, EMAIL);



        verify(deletedApplicationRepository).save(any());
    }

    @Test
    public void hideApplicationFromDashboard() {
        long applicationId = 1L;
        long userId = 2L;
        Application application = newApplication()
                .build();
        User user = new User();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ServiceResult<Void> result = service.hideApplicationFromDashboard(ApplicationUserCompositeId.id(applicationId, userId));

        verify(applicationHiddenFromDashboardRepository).save(any());
    }

}