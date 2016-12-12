package org.innovateuk.ifs.project.finance;

import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.worth.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectFinanceServiceImplTest {

    @InjectMocks
    private com.worth.ifs.project.finance.ProjectFinanceServiceImpl service;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private ApplicationService applicationService;

    @Test
    public void saveSpendProfile() {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        when(projectFinanceRestService.saveSpendProfile(projectId, organisationId, table)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.saveSpendProfile(projectId, organisationId, table);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markSpendProfile() {

        Long projectId = 1L;
        Long organisationId = 1L;
        Boolean complete = true;

        when(projectFinanceRestService.markSpendProfile(projectId, organisationId, complete)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.markSpendProfile(projectId, organisationId, complete);

        assertTrue(result.isSuccess());

    }

    @Test
    public void testCompleteSpendProfileReview() {
        Long projectId = 1L;
        when(projectFinanceRestService.completeSpendProfilesReview(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.completeSpendProfilesReview(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void approveOrRejectSpendProfile() {
        Long projectId = 201L;

        when(projectFinanceRestService.acceptOrRejectSpendProfile(projectId, ApprovalType.APPROVED)).thenReturn(restSuccess());
        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.APPROVED);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getSpendProfileStatusByProjectId() {
        Long projectId = 201L;

        when(projectFinanceRestService.getSpendProfileStatusByProjectId(projectId)).thenReturn(restSuccess(ApprovalType.APPROVED));
        ApprovalType result = service.getSpendProfileStatusByProjectId(projectId);

        assertEquals(ApprovalType.APPROVED, result);

        when(projectFinanceRestService.getSpendProfileStatusByProjectId(projectId)).thenReturn(restSuccess(ApprovalType.REJECTED));
        result = service.getSpendProfileStatusByProjectId(projectId);

        assertEquals(ApprovalType.REJECTED, result);

        when(projectFinanceRestService.getSpendProfileStatusByProjectId(projectId)).thenReturn(restSuccess(ApprovalType.UNSET));
        result = service.getSpendProfileStatusByProjectId(projectId);

        assertEquals(ApprovalType.UNSET, result);
    }

    @Test
    public void testFinanceTotals() {

        List<ProjectFinanceResource> resources = newProjectFinanceResource().build(2);
        when(projectFinanceRestService.getFinanceTotals(123L)).thenReturn(restSuccess(resources));

        List<ProjectFinanceResource> financeTotals = service.getFinanceTotals(123L);
        assertEquals(resources, financeTotals);
    }
}
