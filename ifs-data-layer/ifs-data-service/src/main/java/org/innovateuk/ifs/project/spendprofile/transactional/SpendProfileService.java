package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

import static org.innovateuk.ifs.project.resource.ApprovalType.APPROVED;

/**
 * Service dealing with Project finance operations
 */
public interface SpendProfileService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'external_finance')")
    @SecuredBySpring(value = "GENERATE_SPEND_PROFILE", securedType = ProjectResource.class, description = "A member of the internal Finance Team can generate a Spend Profile for any Project" )
    @Activity(projectId = "projectId", type = ActivityType.SPEND_PROFILE_GENERATED)
    ServiceResult<Void> generateSpendProfile(Long projectId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "GENERATE_SPEND_PROFILE", description = "A member of the internal Finance Team can generate a Spend Profile for a given Project and Organisation" )
    ServiceResult<Void> generateSpendProfileForPartnerOrganisation(Long projectId, Long organisationId, Long userId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "GENERATE_SPEND_PROFILE", securedType = ProjectResource.class, description = "A member of the internal Finance Team can approve or reject a Spend Profile for any Project" )
    @Activity(projectId = "projectId", dynamicType = "approveOrRejectActivityType")
    ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    default Optional<ActivityType> approveOrRejectActivityType(Long projectId, ApprovalType approvalType) {
        return APPROVED == approvalType ? Optional.of(ActivityType.SPEND_PROFILE_APPROVED) : Optional.of(ActivityType.SPEND_PROFILE_REJECTED);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_SPEND_PROFILE_STATUS')")
    ServiceResult<ApprovalType> getSpendProfileStatusByProjectId(Long projectId);

    @NotSecured(value = "This Service is only used within a secured service for performing validation checks (update of project manager and address)", mustBeSecuredByOtherServices = true)
    ServiceResult<ApprovalType> getSpendProfileStatus(Long projectId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_SPEND_PROFILE')")
    ServiceResult<SpendProfileTableResource> getSpendProfileTable(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_SPEND_PROFILE_CSV')")
    ServiceResult<SpendProfileCSVResource> getSpendProfileCSV(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_SPEND_PROFILE')")
    ServiceResult<SpendProfileResource> getSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'EDIT_SPEND_PROFILE')")
    ServiceResult<Void> saveSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId, SpendProfileTableResource table);

    @PreAuthorize("hasAnyAuthority('system_maintainer', 'project_finance')")
    @SecuredBySpring(value = "DELETE_SPEND_PROFILE", securedType = ProjectResource.class, description = "A member of the internal Finance Team can delete a Spend Profile for any Project" )
    @Activity(projectId = "projectId", type = ActivityType.SPEND_PROFILE_DELETED)
    ServiceResult<Void> deleteSpendProfile(Long projectId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'MARK_SPEND_PROFILE_COMPLETE')")
    ServiceResult<Void> markSpendProfileComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'MARK_SPEND_PROFILE_INCOMPLETE')")
    ServiceResult<Void> markSpendProfileIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'COMPLETE_SPEND_PROFILE_REVIEW')")
    @Activity(projectId = "projectId", type = ActivityType.SPEND_PROFILE_SENT)
    ServiceResult<Void> completeSpendProfilesReview(@P("projectId")Long projectId);

}
