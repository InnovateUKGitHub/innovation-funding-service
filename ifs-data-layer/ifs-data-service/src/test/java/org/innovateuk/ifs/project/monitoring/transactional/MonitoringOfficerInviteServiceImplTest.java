package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.mapper.MonitoringOfficerInviteMapper;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficerInvite;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerInviteRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.MonitoringOfficerInviteResourceBuilder.newMonitoringOfficerInviteResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MonitoringOfficerInviteServiceImplTest extends BaseServiceUnitTest<MonitoringOfficerInviteServiceImpl> {

    @Mock
    private MonitoringOfficerInviteRepository monitoringOfficerInviteRepositoryMock;

    @Mock
    private MonitoringOfficerInviteMapper monitoringOfficerInviteMapperMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Override
    protected MonitoringOfficerInviteServiceImpl supplyServiceUnderTest() {
        return new MonitoringOfficerInviteServiceImpl();
    }

    @Test
    public void getInviteByHash() {
        String hash = "hash";
        MonitoringOfficerInvite invite = new MonitoringOfficerInvite("name", "email", hash, InviteStatus.SENT);
        MonitoringOfficerInviteResource inviteResource = newMonitoringOfficerInviteResource().build();

        when(monitoringOfficerInviteRepositoryMock.getByHash(hash)).thenReturn(invite);
        when(monitoringOfficerInviteMapperMock.mapToResource(invite)).thenReturn(inviteResource);

        MonitoringOfficerInviteResource actualInviteResource = service.getInviteByHash(hash).getSuccess();

        assertEquals(inviteResource, actualInviteResource);

        InOrder inOrder = inOrder(monitoringOfficerInviteRepositoryMock, monitoringOfficerInviteMapperMock);
        inOrder.verify(monitoringOfficerInviteRepositoryMock).getByHash(hash);
        inOrder.verify(monitoringOfficerInviteMapperMock).mapToResource(invite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite() {
        String hash = "hash";
        MonitoringOfficerInvite invite = new MonitoringOfficerInvite("name", "email", hash, InviteStatus.SENT);
        MonitoringOfficerInviteResource inviteResource = newMonitoringOfficerInviteResource().build();

        when(monitoringOfficerInviteRepositoryMock.getByHash(hash)).thenReturn(invite);
        when(monitoringOfficerInviteMapperMock.mapToResource(invite)).thenReturn(inviteResource);

        MonitoringOfficerInviteResource actualInviteResource = service.openInvite(hash).getSuccess();

        assertEquals(inviteResource, actualInviteResource);
        assertEquals(InviteStatus.OPENED, invite.getStatus());

        InOrder inOrder = inOrder(monitoringOfficerInviteRepositoryMock, monitoringOfficerInviteMapperMock);
        inOrder.verify(monitoringOfficerInviteRepositoryMock).getByHash(hash);
        inOrder.verify(monitoringOfficerInviteMapperMock).mapToResource(invite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_notFound() {
        String hash = "hash";

        when(monitoringOfficerInviteRepositoryMock.getByHash(hash)).thenReturn(null);

        ServiceResult<MonitoringOfficerInviteResource> result = service.openInvite(hash);

        assertTrue(result.isFailure());

        verify(monitoringOfficerInviteRepositoryMock, only()).getByHash(hash);
    }

    @Test
    public void inviteUnregisteredMonitoringOfficer() {

        User user = newUser()
                .withFirstName("Donald")
                .withLastName("Tusk")
                .withEmailAddress("test@test.test")
                .build();
        Project project = newProject()
                .withApplication(newApplication()
                                         .withCompetition(newCompetition().build())
                                         .build())
                .build();
        when(userRepositoryMock.existsById(user.getId())).thenReturn(true);
        when(monitoringOfficerInviteRepositoryMock.existsByStatusAndUserId(InviteStatus.OPENED, user.getId()))
                .thenReturn(false);
        when(monitoringOfficerInviteRepositoryMock.save(any(MonitoringOfficerInvite.class)))
                .thenReturn(new MonitoringOfficerInvite());
        when(notificationServiceMock.sendNotificationWithFlush(any(Notification.class), any(NotificationMedium.class)))
                .thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.inviteMonitoringOfficer(user, project);

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(monitoringOfficerInviteRepositoryMock, notificationServiceMock);
        inOrder.verify(monitoringOfficerInviteRepositoryMock).existsByStatusAndUserId(InviteStatus.OPENED, user.getId());
        inOrder.verify(monitoringOfficerInviteRepositoryMock).save(any(MonitoringOfficerInvite.class));
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(any(Notification.class), any(NotificationMedium.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteRegisteredMonitoringOfficer() {

        User user = newUser()
                .withFirstName("Michel")
                .withLastName("Barnier")
                .withEmailAddress("test@test.test")
                .build();
        Project project = newProject()
                .withApplication(newApplication()
                                         .withCompetition(newCompetition().build())
                                         .build())
                .build();
        when(userRepositoryMock.existsById(user.getId())).thenReturn(true);
        when(monitoringOfficerInviteRepositoryMock.existsByStatusAndUserId(InviteStatus.OPENED, user.getId()))
                .thenReturn(true);
        when(notificationServiceMock.sendNotificationWithFlush(any(Notification.class), any(NotificationMedium.class)))
                .thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.inviteMonitoringOfficer(user, project);

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(monitoringOfficerInviteRepositoryMock, notificationServiceMock);
        inOrder.verify(monitoringOfficerInviteRepositoryMock).existsByStatusAndUserId(InviteStatus.OPENED, user.getId());
        inOrder.verify(notificationServiceMock).sendNotificationWithFlush(any(Notification.class), any(NotificationMedium.class));
        inOrder.verifyNoMoreInteractions();
    }
}