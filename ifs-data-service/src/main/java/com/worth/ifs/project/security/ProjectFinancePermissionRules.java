package com.worth.ifs.project.security;

import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.user.resource.UserResource;

import static com.worth.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

@PermissionRules
public class ProjectFinancePermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Partners can view their own Spend Profile data")
    public boolean partnersCanViewTheirOwnSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Partners can view their own Spend Profile data")
    public boolean projectFinanceUserCanViewAnySpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Project manager view Spend Profile data")
    public boolean projectManagerCanViewAnySpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isProjectManager(projectOrganisationCompositeId.getProjectId(), user.getId());
    }


    @PermissionRule(
            value = "EDIT_SPEND_PROFILE",
            description = "Partners can edit their own Spend Profile data")
    public boolean partnersCanEditTheirOwnSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId());
    }

    @PermissionRule(value = "MARK_SPEND_PROFILE_COMPLETE", description = "Any partner belonging to organisation can mark its spend profile as complete")
    public boolean partnersCanMarkSpendProfileAsComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId());
    }

    @PermissionRule(value = "COMPLETE_SPEND_PROFILE_REVIEW", description = "Only a project manager can complete the projects spend profiles review")
    public boolean projectManagerCanCompleteSpendProfile(Long projectId, UserResource user) {
        return isProjectManager(projectId, user.getId());
    }

    @PermissionRule(
            value = "VIEW_VIABILITY",
            description = "Project Finance Users can view Viability")
    public boolean projectFinanceUserCanViewViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "SAVE_VIABILITY",
            description = "Project Finance Users can save Viability")
    public boolean projectFinanceUserCanSaveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isProjectFinanceUser(user);
    }


}