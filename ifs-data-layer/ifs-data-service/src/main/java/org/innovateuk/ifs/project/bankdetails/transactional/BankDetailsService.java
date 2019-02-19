package org.innovateuk.ifs.project.bankdetails.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface BankDetailsService {
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<BankDetailsResource> getById(final Long bankDetailsId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<BankDetailsResource> getByProjectAndOrganisation(final Long projectId, final Long organisationId);

    @PreAuthorize("hasPermission(#bankDetailsResource, 'SUBMIT')")
    ServiceResult<Void> submitBankDetails(@P("bankDetailsResource") final BankDetailsResource bankDetailsResource);

    @PreAuthorize("hasPermission(#bankDetailsResource, 'UPDATE')")
    ServiceResult<Void> updateBankDetails(BankDetailsResource bankDetailsResource);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "READ", description = "Project Finance users can see bank details status summary for all partners", securedType = ProjectBankDetailsStatusSummary.class)
    ServiceResult<ProjectBankDetailsStatusSummary> getProjectBankDetailsStatusSummary(final Long projectId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "GET_PENDING_BANK_DETAILS_APPROVALS", description = "Project finance users can get organisations for which Bank Details approval is pending")
    ServiceResult<List<BankDetailsReviewResource>> getPendingBankDetailsApprovals();

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "COUNT_PENDING_BANK_DETAILS_APPROVALS", description = "Project finance users can get count of organisations for which Bank Details approval is pending")
    ServiceResult<Long> countPendingBankDetailsApprovals();
}
