package org.innovateuk.ifs.project.finance;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityResource;

import java.util.List;

/**
 * A service for dealing with a Project's finance operations
 */
public interface ProjectFinanceService {

    @NotSecured("Not currently secured")
    List<ProjectFinanceResource> getProjectFinances(Long projectId);

    @NotSecured("Not currently secured")
    ViabilityResource getViability(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> saveViability(Long projectId, Long organisationId, Viability viability, ViabilityRagStatus viabilityRagRating);

    @NotSecured("Not currently secured")
    EligibilityResource getEligibility(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> saveEligibility(Long projectId, Long organisationId, Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus);

    @NotSecured("Not currently secured")
    boolean isCreditReportConfirmed(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed);

    @NotSecured("Not currently secured")
    List<ProjectFinanceResource> getProjectFinanceTotals(Long projectId);

    @NotSecured("Not currently secured")
    ProjectFinanceResource addProjectFinance(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    ProjectFinanceResource getProjectFinance(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    ValidationMessages addCost(Long applicationFinanceId , Long questionId);
}
