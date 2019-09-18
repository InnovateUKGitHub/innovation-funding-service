package org.innovateuk.ifs.project.bankdetails.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface BankDetailsService {
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<BankDetailsResource> getById(Long bankDetailsId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<BankDetailsResource> getByProjectAndOrganisation(Long projectId, Long organisationId);

    @PreAuthorize("hasPermission(#bankDetailsResource, 'SUBMIT')")
    @Activity(type = ActivityType.BANK_DETAILS_SUBMITTED, projectOrganisationCompositeId = "projectOrganisationCompositeId")
    ServiceResult<Void> submitBankDetails(ProjectOrganisationCompositeId projectOrganisationCompositeId, BankDetailsResource bankDetailsResource);

    @PreAuthorize("hasPermission(#bankDetailsResource, 'UPDATE')")
    @Activity(dynamicType = "bankDetailsActivityType", projectOrganisationCompositeId = "projectOrganisationCompositeId")
    ServiceResult<Void> updateBankDetails(ProjectOrganisationCompositeId projectOrganisationCompositeId, BankDetailsResource bankDetailsResource);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    default Optional<ActivityType> bankDetailsActivityType(ProjectOrganisationCompositeId projectOrganisationCompositeId, BankDetailsResource bankDetailsResource) {
        if (bankDetailsResource.isManualApproval()) {
            return Optional.of(ActivityType.BANK_DETAILS_APPROVED);
        } else {
            return Optional.of(ActivityType.BANK_DETAILS_EDITED);
        }
    }

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "READ", description = "Project Finance users can see bank details status summary for all partners", securedType = ProjectBankDetailsStatusSummary.class)
    ServiceResult<ProjectBankDetailsStatusSummary> getProjectBankDetailsStatusSummary(Long projectId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "GET_PENDING_BANK_DETAILS_APPROVALS", description = "Project finance users can get organisations for which Bank Details approval is pending")
    ServiceResult<List<BankDetailsReviewResource>> getPendingBankDetailsApprovals();

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "COUNT_PENDING_BANK_DETAILS_APPROVALS", description = "Project finance users can get count of organisations for which Bank Details approval is pending")
    ServiceResult<Long> countPendingBankDetailsApprovals();
}
