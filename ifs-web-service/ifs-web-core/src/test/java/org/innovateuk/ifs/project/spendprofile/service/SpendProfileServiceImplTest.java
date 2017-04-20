package org.innovateuk.ifs.project.spendprofile.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpendProfileServiceImplTest {

    @InjectMocks
    private SpendProfileServiceImpl service;

    @Mock
    private SpendProfileRestService spendProfileRestService;


    @Test
    public void saveSpendProfile() {
        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        when(spendProfileRestService.saveSpendProfile(projectId, organisationId, table)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.saveSpendProfile(projectId, organisationId, table);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markSpendProfileComplete() {
        Long projectId = 1L;
        Long organisationId = 1L;

        when(spendProfileRestService.markSpendProfileComplete(projectId, organisationId)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.markSpendProfileComplete(projectId, organisationId);

        assertTrue(result.isSuccess());

    }

    @Test
    public void markSpendProfileIncomplete() {
        Long projectId = 1L;
        Long organisationId = 1L;

        when(spendProfileRestService.markSpendProfileIncomplete(projectId, organisationId)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.markSpendProfileIncomplete(projectId, organisationId);

        assertTrue(result.isSuccess());

    }

    @Test
    public void testCompleteSpendProfileReview() {
        Long projectId = 1L;
        when(spendProfileRestService.completeSpendProfilesReview(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.completeSpendProfilesReview(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void approveOrRejectSpendProfile() {
        Long projectId = 201L;

        when(spendProfileRestService.acceptOrRejectSpendProfile(projectId, ApprovalType.APPROVED)).thenReturn(restSuccess());
        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.APPROVED);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getSpendProfileStatusByProjectId() {
        Long projectId = 201L;

        when(spendProfileRestService.getSpendProfileStatusByProjectId(projectId)).thenReturn(restSuccess(ApprovalType.APPROVED));
        ApprovalType result = service.getSpendProfileStatusByProjectId(projectId);

        assertEquals(ApprovalType.APPROVED, result);

        when(spendProfileRestService.getSpendProfileStatusByProjectId(projectId)).thenReturn(restSuccess(ApprovalType.REJECTED));
        result = service.getSpendProfileStatusByProjectId(projectId);

        assertEquals(ApprovalType.REJECTED, result);

        when(spendProfileRestService.getSpendProfileStatusByProjectId(projectId)).thenReturn(restSuccess(ApprovalType.UNSET));
        result = service.getSpendProfileStatusByProjectId(projectId);

        assertEquals(ApprovalType.UNSET, result);
    }

}
