package org.innovateuk.ifs.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Base class for permission rules
 */
@Component
public class BasePermissionRules {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    public boolean isLeadApplicant(Long applicationId, UserResource loggedInUser) {
        return loggedInUser.getId() == getLeadApplicantId(applicationId);
    }

    private long getLeadApplicantId(Long applicationId) {
        return userService.getLeadApplicantProcessRoleOrNull(applicationId).getUser();
    }

    public boolean applicationNotYetSubmitted(Long applicationId) {
        ApplicationResource applicationResource = getApplication(applicationId);
        return !applicationResource.isSubmitted();
    }

    private ApplicationResource getApplication(Long applicationId){
        return applicationService.getById(applicationId);
    }
}
