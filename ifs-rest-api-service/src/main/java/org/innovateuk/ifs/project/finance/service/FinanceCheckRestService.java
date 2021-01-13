package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.finance.resource.*;

/**
 * Rest Service for dealing with Project finance operations
 */
public interface FinanceCheckRestService {

    RestResult<FinanceCheckResource> getByProjectAndOrganisation(Long projectId, Long organisationId);

    RestResult<Void> update(FinanceCheckResource financeCheckResource);

    RestResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);

    RestResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId);

    RestResult<Void> approveFinanceCheck(Long projectId, Long organisationId);

    RestResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(Long projectId, Long organisationId);

    RestResult<ViabilityResource> getViability(Long projectId, Long organisationId);

    RestResult<Void> saveViability(Long projectId, Long organisationId, ViabilityState viability, ViabilityRagStatus viabilityRagStatus);

    RestResult<EligibilityResource> getEligibility(Long projectId, Long organisationId);

    RestResult<Void> saveEligibility(Long projectId, Long organisationId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus);

    RestResult<Void> approvePaymentMilestoneState(Long projectId, Long organisationId);

    RestResult<Void> resetPaymentMilestoneState(Long projectId, Long organisationId);

    RestResult<ProjectProcurementMilestoneResource> getPaymentMilestoneState(Long projectId, Long organisationId);

    RestResult<Boolean> viewPaymentMilestones(Long projectId, Long organisationId);
}
