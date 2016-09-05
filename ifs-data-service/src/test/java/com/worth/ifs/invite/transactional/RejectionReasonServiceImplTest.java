package com.worth.ifs.invite.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.invite.domain.RejectionReason;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.util.List;

import static com.worth.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class RejectionReasonServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private RejectionReasonService rejectionReasonService = new RejectionReasonServiceImpl();

    @Test
    public void findAllActive() throws Exception {
        List<RejectionReasonResource> rejectionReasonResources = newRejectionReasonResource().build(2);

        List<RejectionReason> rejectionReasons = newRejectionReason().build(2);

        when(rejectionReasonRepositoryMock.findByActiveTrueOrderByPriorityAsc()).thenReturn(rejectionReasons);
        when(rejectionReasonMapperMock.mapToResource(same(rejectionReasons.get(0)))).thenReturn(rejectionReasonResources.get(0));
        when(rejectionReasonMapperMock.mapToResource(same(rejectionReasons.get(1)))).thenReturn(rejectionReasonResources.get(1));

        List<RejectionReasonResource> found = rejectionReasonService.findAllActive().getSuccessObjectOrThrowException();
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

        when(rejectionReasonRepositoryMock.findOne(rejectionReasonId)).thenReturn(rejectionReason);
        when(rejectionReasonMapperMock.mapToResource(same(rejectionReason))).thenReturn(rejectionReasonResource);

        RejectionReasonResource found = rejectionReasonService.findById(rejectionReasonId).getSuccessObjectOrThrowException();
        assertEquals(rejectionReasonResource, found);

        InOrder inOrder = inOrder(rejectionReasonRepositoryMock, rejectionReasonMapperMock);
        inOrder.verify(rejectionReasonRepositoryMock, calls(1)).findOne(rejectionReasonId);
        inOrder.verify(rejectionReasonMapperMock, calls(1)).mapToResource(rejectionReason);
        inOrder.verifyNoMoreInteractions();
    }

}