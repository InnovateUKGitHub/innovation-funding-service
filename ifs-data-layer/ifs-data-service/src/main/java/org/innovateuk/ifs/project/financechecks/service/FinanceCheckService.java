package org.innovateuk.ifs.project.financechecks.service;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.FinanceCheck;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

/**
 * A service for finance check functionality
 */
@SuppressWarnings("DefaultAnnotationParam")
public interface FinanceCheckService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW", securedType = FinanceCheck.class, description = "Project finance user should be able to view any finance check")
    ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key);

    @PreAuthorize("hasAnyAuthority('project_finance', 'external_finance')")
    @SecuredBySpring(value = "VIEW", securedType = FinanceCheckSummaryResource.class, description = "Project finance users have the ability to view a summary of finance checks status for all partners" )
    ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(long projectId);

    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    ServiceResult<Boolean> isQueryActionRequired(long projectId, long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ_ELIGIBILITY')")
    ServiceResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(long projectId, long organisationId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<ProjectFinanceResource>> getProjectFinances(long projectId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_VIABILITY')")
    ServiceResult<ViabilityResource> getViability(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'SAVE_VIABILITY')")
    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", dynamicType = "viabilityActivityType")
    ServiceResult<Void> saveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, ViabilityState viability, ViabilityRagStatus viabilityRagStatus);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'RESET_VIABILITY')")
    @Activity(projectId = "projectId", type = ActivityType.VIABILITY_RESET)
    ServiceResult<Void> resetViability(Long projectId, Long organisationId, String reason);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'RESET_ELIGIBILITY')")
    @Activity(projectId = "projectId", type = ActivityType.ELIGIBILITY_RESET)
    ServiceResult<Void> resetEligibility(Long projectId, Long organisationId, String reason);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'RESET_FINANCE_CHECKS')")
    @Activity(projectId = "projectId", type = ActivityType.FINANCE_CHECKS_RESET)
    ServiceResult<Void> resetFinanceChecks(Long projectId);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    default Optional<ActivityType> viabilityActivityType(ProjectOrganisationCompositeId projectOrganisationCompositeId, ViabilityState viability, ViabilityRagStatus viabilityRagStatus) {
        return viability == ViabilityState.APPROVED ? Optional.of(ActivityType.VIABILITY_APPROVED) : Optional.empty();
    }

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_ELIGIBILITY')")
    ServiceResult<EligibilityResource> getEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'SAVE_ELIGIBILITY')")
    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", dynamicType = "eligibilityActivityType")
    ServiceResult<Void> saveEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    default Optional<ActivityType> eligibilityActivityType(ProjectOrganisationCompositeId projectOrganisationCompositeId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus) {
        return eligibility == EligibilityState.APPROVED ? Optional.of(ActivityType.ELIGIBILITY_APPROVED) : Optional.empty();
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'SAVE_CREDIT_REPORT')")
    ServiceResult<Void> saveCreditReport(long projectId, long organisationId, boolean reportPresent);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'VIEW_CREDIT_REPORT')")
    ServiceResult<Boolean> getCreditReport(long projectId, long organisationId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'SAVE_MILESTONE_CHECK')")
    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", type = ActivityType.PAYMENT_MILESTONES_APPROVED)
    ServiceResult<Void> approvePaymentMilestoneState(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'RESET_MILESTONE_CHECK')")
    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", type = ActivityType.PAYMENT_MILESTONES_RESET)
    ServiceResult<Void> resetPaymentMilestoneState(ProjectOrganisationCompositeId projectOrganisationCompositeId, String reason);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_MILESTONE_STATUS')")
    ServiceResult<PaymentMilestoneResource> getPaymentMilestone(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_FUNDING_RULES')")
    ServiceResult<FundingRulesResource> getFundingRules(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'SAVE_FUNDING_RULES')")
    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", type = ActivityType.FUNDING_RULES_UPDATED)
    ServiceResult<Void> saveFundingRules(ProjectOrganisationCompositeId projectOrganisationCompositeId, FundingRules fundingRules);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'SAVE_FUNDING_RULES')")
    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", type = ActivityType.FUNDING_RULES_APPROVED)
    ServiceResult<Void> approveFundingRules(ProjectOrganisationCompositeId projectOrganisationCompositeId);

}
