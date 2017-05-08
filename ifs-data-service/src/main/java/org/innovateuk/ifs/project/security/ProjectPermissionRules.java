package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.*;

@PermissionRules
@Component
public class ProjectPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "A user can see projects that they are partners on")
    public boolean partnersOnProjectCanView(ProjectResource project, UserResource user) {
        return project != null && isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Internal users can see project resources")
    public boolean internalUsersCanViewProjects(final ProjectResource project, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "UPDATE_BASIC_PROJECT_SETUP_DETAILS",
            description = "The lead partners can update the basic project details, like start date, address, Project Manager")
    public boolean leadPartnersCanUpdateTheBasicProjectDetails(ProjectResource project, UserResource user) {
        return isLeadPartner(project.getId(), user.getId());
    }


    @PermissionRule(
            value = "UPDATE_FINANCE_CONTACT",
            description = "A partner can update the finance contact for their own organisation")
    public boolean partnersCanUpdateTheirOwnOrganisationsFinanceContacts(ProjectOrganisationCompositeId composite, UserResource user) {
        return isPartner(composite.getProjectId(), user.getId()) && partnerBelongsToOrganisation(composite.getProjectId(), user.getId(), composite.getOrganisationId());
    }

    @PermissionRule(
            value = "UPDATE_FINANCE_CONTACT",
            description = "A partner can update the finance contact for their own organisation")
    public boolean submitIsAllowed(Long projectId, UserResource user) {
        return isPartner(projectId, user.getId());
    }

    @PermissionRule(
            value = "VIEW_TEAM_STATUS",
            description = "All partners can view team status")
    public boolean partnersCanViewTeamStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_TEAM_STATUS",
            description = "Internal users can see a team's status")
    public boolean internalUsersCanViewTeamStatus(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "VIEW_STATUS",
            description = "All partners can view the project status")
    public boolean partnersCanViewStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_STATUS",
            description = "Internal users can see the project status")
    public boolean internalUsersCanViewStatus(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

}
