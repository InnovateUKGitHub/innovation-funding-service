package org.innovateuk.ifs.project.finance.service;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.resource.SpendProfileResource;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;

import java.util.List;

/**
 * Rest Service for dealing with Project finance operations
 */
public interface ProjectFinanceRestService {

    RestResult<List<ProjectFinanceResource>> getProjectFinances(Long projectId);

    RestResult<ProjectFinanceResource> getProjectFinance(Long projectId, Long organisationId);

    RestResult<ViabilityResource> getViability(Long projectId, Long organisationId);

    RestResult<Void> saveViability(Long projectId, Long organisationId, Viability viability, ViabilityRagStatus viabilityRagStatus);

    RestResult<EligibilityResource> getEligibility(Long projectId, Long organisationId);

    RestResult<Void> saveEligibility(Long projectId, Long organisationId, Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus);

    RestResult<Boolean> isCreditReportConfirmed(Long projectId, Long organisationId);

    RestResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed);

    RestResult<List<ProjectFinanceResource>> getFinanceTotals(Long applicationId);

    RestResult<ProjectFinanceResource> addProjectFinanceForOrganisation(Long projectId, Long organisationId);
}
