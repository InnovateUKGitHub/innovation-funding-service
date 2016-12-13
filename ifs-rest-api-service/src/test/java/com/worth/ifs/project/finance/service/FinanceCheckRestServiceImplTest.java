package com.worth.ifs.project.finance.service;

import com.worth.ifs.BaseRestServiceUnitTest;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.resource.ApprovalType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class FinanceCheckRestServiceImplTest extends BaseRestServiceUnitTest<FinanceCheckRestServiceImpl> {

    @Override
    protected FinanceCheckRestServiceImpl registerRestServiceUnderTest() {
        return new FinanceCheckRestServiceImpl();
    }

    @Test
    @Ignore
    public void testApprove() {
        setupPostWithRestResultExpectations("/project/123/partner-organisation/456/finance-check/approve", OK);
        service.approveFinanceCheck(123L, 456L);
        setupPostWithRestResultVerifications("/project/123/partner-organisation/456/finance-check/approve", Void.class, null);
    }

    @Test
    @Ignore
    public void testGetFinanceCheckApprovalStatus() {
        //TODO nuno
        //FinanceCheckState currentState, ProjectUserResource participant, UserResource internalParticipant, LocalDateTime modifiedDate, boolean canApprove
        FinanceCheckProcessResource processStatus = new FinanceCheckProcessResource(null, null, null,null, false);
        setupGetWithRestResultExpectations("/project/123/partner-organisation/456/finance-check/status", FinanceCheckProcessResource.class, processStatus);

        Assert.assertEquals(processStatus, service.getFinanceCheckApprovalStatus(123L, 456L).getSuccessObject());
    }

    @Test
    public void markCreditReport() {

        Long projectId = 1L;
        Long organisationId = 1L;
        Boolean complete = true;

        setupPostWithRestResultExpectations("/finance-check/" + projectId + "/partner-organisation/" + organisationId + "/credit-report/" + complete,
                OK);

        RestResult<Void> result = service.setCreditReport(projectId, organisationId, complete);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getCreditReport() {
        Long projectId = 1L;
        Long organisationId = 1L;

        setupGetWithRestResultExpectations("/finance-check/" + projectId + "/partner-organisation/" + organisationId + "/credit-report",
                Boolean.class,
                Boolean.TRUE,
                OK);

        RestResult<Boolean> result = service.getCreditReport(projectId, organisationId);
        assertTrue(result.isSuccess());
        Assert.assertEquals(Boolean.TRUE, result.getSuccessObject());
    }
}
