package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Permission checker around the access to Application
 */
@PermissionRules
@Component
public class ApplicationPermissionRules {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @PermissionRule(value = "LEAD_APPLICANT", description = "The logged in user is the lead applicant")
    public boolean isLeadApplicant(Long applicationId, UserResource loggedInUser) {
        ApplicationResource applicationResource = getApplication(applicationId);
        //return loggedInUser.getId() == getLeadApplicantId(applicationResource);
        boolean flag =  loggedInUser.getId() == getLeadApplicantId(applicationResource);
        return flag;
    }

    private long getLeadApplicantId(ApplicationResource applicationResource) {
        //return userService.getLeadApplicantProcessRoleOrNull(applicationResource).getUser();
        ProcessRoleResource processRoleResource = userService.getLeadApplicantProcessRoleOrNull(applicationResource);
        Long userId = processRoleResource.getUser();
        return userId;
    }

    @PermissionRule(value = "APPLICATION_NOT_YET_SUBMITTED", description = "The application is not yet submitted")
    public boolean applicationNotYetSubmitted(Long applicationId, UserResource loggedInUser) {
        ApplicationResource applicationResource = getApplication(applicationId);
        return !applicationResource.hasBeenSubmitted();
    }

    private ApplicationResource getApplication(Long applicationId){
        return applicationService.getById(applicationId);
    }
}
