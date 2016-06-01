package com.worth.ifs.project.security;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;

@PermissionRules
@Component
public class ProjectPermissionRules {
    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @PermissionRule(value = "READ", description = "A user can see an project resource which they are connected to")
    public boolean usersConnectedToTheProjectCanView(ProjectResource project, UserResource user) {
        boolean isConnectedToProject = userIsConnectedToProjectResource(project, user);
        return isConnectedToProject;
    }

    @PermissionRule(value = "READ", description = "Comp admins can see project resources")
    public boolean compAdminsCanViewProjects(final ProjectResource project, final UserResource user){
        return isCompAdmin(user);
    }

    public boolean userIsConnectedToProjectResource(ProjectResource project, UserResource user) {
        ProcessRole processRole =  processRoleRepository.findByUserIdAndApplicationId(user.getId(), project.getId());
        return processRole != null;
    }
}

