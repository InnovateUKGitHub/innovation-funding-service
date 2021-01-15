package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.finance.resource.*;
import org.junit.Assert;
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
    public void testApprove() {
        setupPostWithRestResultExpectations("/project/123/partner-organisation/456/finance-check/approve", OK);
        service.approveFinanceCheck(123L, 456L);
        setupPostWithRestResultVerifications("/project/123/partner-organisation/456/finance-check/approve", Void.class);
    }

    @Test
    public void testGetFinanceCheckEligibility() {
        FinanceCheckEligibilityResource processStatus = new FinanceCheckEligibilityResource();
        setupGetWithRestResultExpectations("/project/123/partner-organisation/456/finance-check/eligibility", FinanceCheckEligibilityResource.class, processStatus);

        Assert.assertEquals(processStatus, service.getFinanceCheckEligibilityDetails(123L, 456L).getSuccess());
    }

    @Test
    public void testGetFinanceCheckOverview() {
        FinanceCheckOverviewResource processStatus = new FinanceCheckOverviewResource();
        setupGetWithRestResultExpectations("/project/123/finance-check/overview", FinanceCheckOverviewResource.class, processStatus);

        Assert.assertEquals(processStatus, service.getFinanceCheckOverview(123L).getSuccess());
    }

    @Test
    public void getViability() {

        ViabilityResource viability = new ViabilityResource(ViabilityState.APPROVED, ViabilityRagStatus.GREEN);

        setupGetWithRestResultExpectations( "/project/123/partner-organisation/456/viability", ViabilityResource.class, viability);

        RestResult<ViabilityResource> results = service.getViability(123L, 456L);

        assertEquals(ViabilityState.APPROVED, results.getSuccess().getViability());
        assertEquals(ViabilityRagStatus.GREEN, results.getSuccess().getViabilityRagStatus());
    }

    @Test
    public void saveViability() {

        String postUrl = "/project/123/partner-organisation/456/viability/" +
                ViabilityState.APPROVED.name() + "/" + ViabilityRagStatus.RED.name();

        setupPostWithRestResultExpectations(postUrl, OK);

        RestResult<Void> result = service.saveViability(123L, 456L, ViabilityState.APPROVED, ViabilityRagStatus.RED);

        assertTrue(result.isSuccess());

        setupPostWithRestResultVerifications(postUrl, Void.class);
    }

    @Test
    public void getEligibility() {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);

        setupGetWithRestResultExpectations( "/project/123/partner-organisation/456/eligibility", EligibilityResource.class, eligibility);

        RestResult<EligibilityResource> results = service.getEligibility(123L, 456L);

        assertEquals(EligibilityState.APPROVED, results.getSuccess().getEligibility());
        assertEquals(EligibilityRagStatus.GREEN, results.getSuccess().getEligibilityRagStatus());
    }

    @Test
    public void saveEligibility() {

        String postUrl = "/project/123/partner-organisation/456/eligibility/" +
                EligibilityState.APPROVED.name() + "/" + EligibilityRagStatus.RED.name();

        setupPostWithRestResultExpectations(postUrl, OK);

        RestResult<Void> result = service.saveEligibility(123L, 456L, EligibilityState.APPROVED, EligibilityRagStatus.RED);

        assertTrue(result.isSuccess());

        setupPostWithRestResultVerifications(postUrl, Void.class);
    }
}
