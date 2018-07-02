package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.mapper.RejectionReasonMapper;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

public class RejectionReasonServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private RejectionReasonService rejectionReasonService = new RejectionReasonServiceImpl();

    @Mock
    private RejectionReasonRepository rejectionReasonRepositoryMock;

    @Mock
    private RejectionReasonMapper rejectionReasonMapperMock;

    @Test
    public void findAllActive() throws Exception {
        List<RejectionReasonResource> rejectionReasonResources = newRejectionReasonResource().build(2);

        List<RejectionReason> rejectionReasons = newRejectionReason().build(2);

        when(rejectionReasonRepositoryMock.findByActiveTrueOrderByPriorityAsc()).thenReturn(rejectionReasons);
        when(rejectionReasonMapperMock.mapToResource(same(rejectionReasons.get(0)))).thenReturn(rejectionReasonResources.get(0));
        when(rejectionReasonMapperMock.mapToResource(same(rejectionReasons.get(1)))).thenReturn(rejectionReasonResources.get(1));

        List<RejectionReasonResource> found = rejectionReasonService.findAllActive().getSuccess();
        assertEquals(rejectionReasonResources, found);

        InOrder inOrder = inOrder(rejectionReasonRepositoryMock, rejectionReasonMapperMock);
        inOrder.verify(rejectionReasonRepositoryMock, calls(1)).findByActiveTrueOrderByPriorityAsc();
        inOrder.verify(rejectionReasonMapperMock, calls(2)).mapToResource(isA(RejectionReason.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findById() throws Exception {
        Long rejectionReasonId = 1L;

        RejectionReasonResource rejectionReasonResource = newRejectionReasonResource().build();

        RejectionReason rejectionReason = newRejectionReason().build();

        when(rejectionReasonRepositoryMock.findById(rejectionReasonId)).thenReturn(Optional.of(rejectionReason));
        when(rejectionReasonMapperMock.mapToResource(same(rejectionReason))).thenReturn(rejectionReasonResource);

        RejectionReasonResource found = rejectionReasonService.findById(rejectionReasonId).getSuccess();
        assertEquals(rejectionReasonResource, found);

        InOrder inOrder = inOrder(rejectionReasonRepositoryMock, rejectionReasonMapperMock);
        inOrder.verify(rejectionReasonRepositoryMock, calls(1)).findById(rejectionReasonId);
        inOrder.verify(rejectionReasonMapperMock, calls(1)).mapToResource(rejectionReason);
        inOrder.verifyNoMoreInteractions();
    }

}
