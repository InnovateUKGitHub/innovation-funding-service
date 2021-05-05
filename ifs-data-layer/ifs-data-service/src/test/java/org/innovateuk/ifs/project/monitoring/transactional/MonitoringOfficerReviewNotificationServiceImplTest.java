package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerReviewNotificationServiceImplTest {

    @InjectMocks
    private MonitoringOfficerReviewNotificationServiceImpl service;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private NotificationService notificationServiceMock;

    @Test
    public void sendDocumentReviewNotification() {

        User moUser = newUser()
                .withFirstName("Charlie")
                .withLastName("Bravo")
                .withEmailAddress("charlie.bravo@test123.com")
                .build();
        Project project = newProject()
                .withApplication(newApplication()
                        .withCompetition(newCompetition().build())
                        .build())
                .build();
        when(notificationServiceMock.sendNotificationWithFlush(any(Notification.class), any(NotificationMedium.class)))
                .thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.sendDocumentReviewNotification(moUser, project);

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(notificationServiceMock);
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(any(Notification.class), any(NotificationMedium.class));
        inOrder.verifyNoMoreInteractions();
    }
}
