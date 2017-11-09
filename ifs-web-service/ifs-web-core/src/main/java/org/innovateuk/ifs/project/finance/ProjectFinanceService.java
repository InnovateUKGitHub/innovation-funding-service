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

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProjectFinanceResource> getProjectFinances(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ViabilityResource getViability(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> saveViability(Long projectId, Long organisationId, Viability viability, ViabilityRagStatus viabilityRagRating);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    EligibilityResource getEligibility(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> saveEligibility(Long projectId, Long organisationId, Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    boolean isCreditReportConfirmed(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProjectFinanceResource> getProjectFinanceTotals(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ProjectFinanceResource addProjectFinance(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ProjectFinanceResource getProjectFinance(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ValidationMessages addCost(Long applicationFinanceId , Long questionId);
}
