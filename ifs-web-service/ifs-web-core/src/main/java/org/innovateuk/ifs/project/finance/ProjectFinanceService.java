package org.innovateuk.ifs.project.finance;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
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
import java.util.Optional;

/**
 * A service for dealing with a Project's finance operations
 */
public interface ProjectFinanceService {

    List<ProjectFinanceResource> getProjectFinances(Long projectId);

    ViabilityResource getViability(Long projectId, Long organisationId);

    ServiceResult<Void> saveViability(Long projectId, Long organisationId, Viability viability, ViabilityRagStatus viabilityRagRating);

    EligibilityResource getEligibility(Long projectId, Long organisationId);

    ServiceResult<Void> saveEligibility(Long projectId, Long organisationId, Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus);

    boolean isCreditReportConfirmed(Long projectId, Long organisationId);

    ServiceResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed);

    List<ProjectFinanceResource> getProjectFinanceTotals(Long projectId);

    ProjectFinanceResource addProjectFinance(Long projectId, Long organisationId);

    ProjectFinanceResource getProjectFinance(Long projectId, Long organisationId);

    ValidationMessages addCost(Long applicationFinanceId , Long questionId);
}
