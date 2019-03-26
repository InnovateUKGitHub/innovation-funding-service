package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.mapper.MonitoringOfficerInviteMapper;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficerInvite;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerInviteRepository;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.innovateuk.ifs.invite.builder.MonitoringOfficerInviteResourceBuilder.newMonitoringOfficerInviteResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MonitoringOfficerInviteServiceImplTest extends BaseServiceUnitTest<MonitoringOfficerInviteServiceImpl> {

    @Mock
    private MonitoringOfficerInviteRepository monitoringOfficerInviteRepositoryMock;

    @Mock
    private MonitoringOfficerInviteMapper monitoringOfficerInviteMapperMock;

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
}