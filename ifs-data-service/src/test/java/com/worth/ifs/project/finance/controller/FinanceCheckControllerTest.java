package com.worth.ifs.project.finance.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.controller.FinanceCheckController;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FinanceCheckControllerTest extends BaseControllerMockMVCTest<FinanceCheckController> {

    @Test
    public void testGetFinanceCheck() {
        Long projectId = 123L;
        Long organisationId = 456L;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(financeCheckServiceMock.getByProjectAndOrganisation(projectOrganisationCompositeId)).thenReturn(serviceSuccess(newFinanceCheckResource().build()));
        RestResult<FinanceCheckResource> result = controller.getFinanceCheck(projectId, organisationId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetFinanceCheckSummary(){
        Long projectId = 123L;
        when(financeCheckServiceMock.getFinanceCheckSummary(projectId)).thenReturn(serviceSuccess(newFinanceCheckSummaryResource().build()));
        RestResult<FinanceCheckSummaryResource> result = controller.getFinanceCheckSummary(projectId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetFinanceCheckApprovalStatus(){
        Long projectId = 123L;
        Long organisationId = 456L;
        when(financeCheckServiceMock.getFinanceCheckApprovalStatus(projectId, organisationId)).thenReturn(serviceSuccess(newFinanceCheckProcessResource().build()));
        RestResult<FinanceCheckProcessResource> result = controller.getFinanceCheckApprovalStatus(projectId, organisationId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testApproveFinanceCheck(){
        Long projectId = 123L;
        Long organisationId = 456L;
        when(financeCheckServiceMock.approve(projectId, organisationId)).thenReturn(serviceSuccess());
        RestResult<Void> result = controller.approveFinanceCheck(projectId, organisationId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateFinanceCheck() {
        FinanceCheckResource financeCheckResource = newFinanceCheckResource().build();
        when(financeCheckServiceMock.save(financeCheckResource)).thenReturn(serviceSuccess());
        RestResult<Void> result = controller.updateFinanceCheck(financeCheckResource);
        assertTrue(result.isSuccess());
    }

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}