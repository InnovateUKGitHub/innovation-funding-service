package org.innovateuk.ifs.project.financechecks.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.financechecks.domain.FinanceCheck;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * A service for finance check functionality
 */
public interface FinanceCheckService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW", securedType = FinanceCheck.class, description = "Project finance user should be able to view any finance check")
    ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW", securedType = FinanceCheckSummaryResource.class, description = "Project finance users have the ability to view a summary of finance checks status for all partners" )
    ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'READ_OVERVIEW')")
    ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId);

    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    ServiceResult<Boolean> isQueryActionRequired(Long projectId, Long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ_ELIGIBILITY')")
    ServiceResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(Long projectId, Long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Long> getTurnoverByOrganisationId(Long applicationId, Long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Long> getHeadCountByOrganisationId(Long applicationId, Long organisationId);

}
