package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.security.SecuredBySpring;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * A service for finance check functionality
 */
public interface FinanceCheckService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW", securedType = FinanceCheck.class, description = "Project finance user should be able to view any finance check")
    ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "EDIT", securedType = FinanceCheck.class, description = "Project finance user should be able to edit any finance check")
    ServiceResult<Void> save(FinanceCheckResource toUpdate);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "APPROVE", securedType = FinanceCheck.class, description = "Project finance users have the ability to approve Finance Checks" )
    ServiceResult<Void> approve(Long projectId, Long organisationId);

    // TODO: Open this up to partners for updating status on project setup status page
    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW", securedType = FinanceCheck.class, description = "Project finance users have the ability to view the current status of Finance Checks approvals" )
    ServiceResult<FinanceCheckProcessResource> getFinanceCheckApprovalStatus(Long projectId, Long organisationId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "VIEW", securedType = FinanceCheckSummaryResource.class, description = "Project finance users have the ability to view a summary of finance checks status for all partners" )
    ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);
}
