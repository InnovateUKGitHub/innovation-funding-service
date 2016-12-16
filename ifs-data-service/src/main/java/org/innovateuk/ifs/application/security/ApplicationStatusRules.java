package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.ApplicationStatus;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ApplicationStatusRules {

    @PermissionRule(value = "READ", description = "for now any authenticated user can read the applicationStatusses")
    public boolean userCanReadApplicationStatus(ApplicationStatus applicationStatus, UserResource user){
        return true;
    }

    @PermissionRule(value = "READ", description = "for now any authenticated user can read the applicationStatusses")
    public boolean userCanReadApplicationStatusResource(ApplicationStatusResource applicationStatus, UserResource user){
        return true;
    }
}
