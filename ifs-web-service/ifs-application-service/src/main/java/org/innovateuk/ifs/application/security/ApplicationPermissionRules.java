package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Permission checker around the access to Application
 */
@PermissionRules
@Component
public class ApplicationPermissionRules extends BasePermissionRules{

    @PermissionRule(value = "ADD_APPLICANT", description = "Allowed to add a new applicant")
    public boolean addApplicant(Long applicationId, UserResource loggedInUser) {
        return isLeadApplicant(applicationId, loggedInUser) && applicationNotYetSubmitted(applicationId);
    }

    @PermissionRule(value = "REMOVE_APPLICANT", description = "Allowed to remove an existing applicant")
    public boolean removeApplicant(Long applicationId, UserResource loggedInUser) {
        return isLeadApplicant(applicationId, loggedInUser) && applicationNotYetSubmitted(applicationId);
    }

    @PermissionRule(value = "VIEW_APPLICATION_TEAM_PAGE", description = "Allowed to view the application team page")
    public boolean viewApplicationTeamPage(Long applicationId, UserResource loggedInUser) {
        return applicationNotYetSubmitted(applicationId);
    }

    @PermissionRule(value = "BEGIN_APPLICATION", description = "Allowed to begin an application")
    public boolean beginApplication(Long applicationId, UserResource loggedInUser) {
        return isLeadApplicant(applicationId, loggedInUser);
    }
}
