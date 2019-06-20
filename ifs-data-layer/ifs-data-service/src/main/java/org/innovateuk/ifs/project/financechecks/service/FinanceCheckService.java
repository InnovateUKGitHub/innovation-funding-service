package org.innovateuk.ifs.project.financechecks.service;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.domain.ActivityType;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.FinanceCheck;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * A service for finance check functionality
 */
@SuppressWarnings("DefaultAnnotationParam")
public interface FinanceCheckService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW", securedType = FinanceCheck.class, description = "Project finance user should be able to view any finance check")
    ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW", securedType = FinanceCheckSummaryResource.class, description = "Project finance users have the ability to view a summary of finance checks status for all partners" )
    ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId);

    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    ServiceResult<Boolean> isQueryActionRequired(Long projectId, Long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ_ELIGIBILITY')")
    ServiceResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(Long projectId, Long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Long> getTurnoverByOrganisationId(Long applicationId, Long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Long> getHeadCountByOrganisationId(Long applicationId, Long organisationId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<ProjectFinanceResource>> getProjectFinances(Long projectId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_VIABILITY')")
    ServiceResult<ViabilityResource> getViability(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'SAVE_VIABILITY')")
    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", type = ActivityType.VIABILITY_APPROVED, condition = "isViabilityApproved")
    ServiceResult<Void> saveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, Viability viability, ViabilityRagStatus viabilityRagStatus);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    default boolean isViabilityApproved(ProjectOrganisationCompositeId projectOrganisationCompositeId, Viability viability, ViabilityRagStatus viabilityRagStatus) {
        return viability == Viability.APPROVED;
    }

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_ELIGIBILITY')")
    ServiceResult<EligibilityResource> getEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'SAVE_ELIGIBILITY')")
    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", type = ActivityType.ELIGIBILITY_APPROVED, condition = "isEligibilityApproved")
    ServiceResult<Void> saveEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    default boolean isEligibilityApproved(ProjectOrganisationCompositeId projectOrganisationCompositeId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus) {
        return eligibility == EligibilityState.APPROVED;
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'SAVE_CREDIT_REPORT')")
    ServiceResult<Void> saveCreditReport(Long projectId, Long organisationId, boolean reportPresent);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'VIEW_CREDIT_REPORT')")
    ServiceResult<Boolean> getCreditReport(Long projectId, Long organisationId);
}
