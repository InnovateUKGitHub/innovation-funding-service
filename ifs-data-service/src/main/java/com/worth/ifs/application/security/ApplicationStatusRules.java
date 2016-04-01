package com.worth.ifs.application.security;

import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.User;

import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ApplicationStatusRules {

    @PermissionRule(value = "READ", description = "for now any authenticated user can read the applicationStatusses")
    public boolean userCanReadApplicationStatus(ApplicationStatus applicationStatus, User user){
        return true;
    }

    @PermissionRule(value = "READ", description = "for now any authenticated user can read the applicationStatusses")
    public boolean userCanReadApplicationStatusResource(ApplicationStatusResource applicationStatus, User user){
        return true;
    }
}
