package com.worth.ifs.project.security;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;

@PermissionRules
@Component
public class ProjectPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "A user can see an project resource which they are connected to")
    public boolean usersConnectedToTheProjectCanView(ProjectResource project, UserResource user) {
        return userIsConnectedToProjectResource(project, user);
    }

    @PermissionRule(value = "READ", description = "Comp admins can see project resources")
    public boolean compAdminsCanViewProjects(final ProjectResource project, final UserResource user){
        return isCompAdmin(user);
    }

    @PermissionRule(
            value = "UPDATE_BASIC_PROJECT_SETUP_DETAILS",
            description = "The lead partner can update the basic project details like start date")
    public boolean updateBasicProjectSetupDetails(ProjectResource project, UserResource user) {
        return isLeadApplicant(project.getApplication(), user);
    }

    public boolean userIsConnectedToProjectResource(ProjectResource project, UserResource user) {
        ProcessRole processRole =  processRoleRepository.findByUserIdAndApplicationId(user.getId(), project.getApplication());
        return processRole != null;
    }
}