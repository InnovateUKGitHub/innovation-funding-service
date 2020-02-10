package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.*;

import java.util.List;

/**
 * Rest Service for dealing with Project finance operations
 */
public interface ProjectFinanceRestService {

    RestResult<List<ProjectFinanceResource>> getProjectFinances(Long projectId);

    RestResult<ProjectFinanceResource> getProjectFinance(Long projectId, Long organisationId);

    RestResult<ViabilityResource> getViability(Long projectId, Long organisationId);

    RestResult<Void> saveViability(Long projectId, Long organisationId, ViabilityState viability, ViabilityRagStatus viabilityRagStatus);

    RestResult<EligibilityResource> getEligibility(Long projectId, Long organisationId);

    RestResult<Void> saveEligibility(Long projectId, Long organisationId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus);

    RestResult<Boolean> isCreditReportConfirmed(Long projectId, Long organisationId);

    RestResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed);

    RestResult<List<ProjectFinanceResource>> getFinanceTotals(Long applicationId);

    RestResult<ProjectFinanceResource> addProjectFinanceForOrganisation(Long projectId, Long organisationId);
}
