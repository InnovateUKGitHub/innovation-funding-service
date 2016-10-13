package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.*;
import com.worth.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service dealing with Project finance operations
 */
public interface ProjectFinanceService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "GENERATE_SPEND_PROFILE", securedType = ProjectResource.class, description = "A member of the internal Finance Team can generate a Spend Profile for any Project" )
    ServiceResult<Void> generateSpendProfile(Long projectId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "GENERATE_SPEND_PROFILE", securedType = ProjectResource.class, description = "A member of the internal Finance Team can approve or reject a Spend Profile for any Project" )
    ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "GENERATE_SPEND_PROFILE", securedType = ProjectResource.class, description = "A member of the internal Finance Team can get the approved status of a Spend Profile for any Project" )
    ServiceResult<ApprovalType> getSpendProfileStatusByProjectId(Long projectId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_SPEND_PROFILE')")
    ServiceResult<SpendProfileTableResource> getSpendProfileTable(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_SPEND_PROFILE') || hasAuthority('comp_admin')")
    ServiceResult<SpendProfileCSVResource> getSpendProfileCSV(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_SPEND_PROFILE')")
    ServiceResult<SpendProfileResource> getSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'EDIT_SPEND_PROFILE')")
    ServiceResult<Void> saveSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId, SpendProfileTableResource table);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'MARK_SPEND_PROFILE_COMPLETE')")
    ServiceResult<Void> markSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId, Boolean complete);

    @PreAuthorize("hasPermission(#projectId, 'COMPLETE_SPEND_PROFILE_REVIEW')")
    ServiceResult<Void> completeSpendProfilesReview(Long projectId);
}
