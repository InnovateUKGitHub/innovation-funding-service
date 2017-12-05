package org.innovateuk.ifs.application.team.security;

import org.innovateuk.ifs.application.resource.ApplicationCompositeId;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Permission checker around the access to Application
 */
@PermissionRules
@Component
public class ApplicationPermissionRules extends BasePermissionRules{

    @PermissionRule(value = "ADD_APPLICANT", description = "Allowed to add a new applicant")
    public boolean addApplicant(ApplicationCompositeId applicationCompositeId, UserResource loggedInUser) {
        return isLeadApplicant(applicationCompositeId.id(), loggedInUser) && applicationNotYetSubmitted(applicationCompositeId.id());
    }

    @PermissionRule(value = "REMOVE_APPLICANT", description = "Allowed to remove an existing applicant")
    public boolean removeApplicant(ApplicationCompositeId applicationCompositeId, UserResource loggedInUser) {
        return isLeadApplicant(applicationCompositeId.id(), loggedInUser) && applicationNotYetSubmitted(applicationCompositeId.id());
    }

    @PermissionRule(value = "VIEW_APPLICATION_TEAM_PAGE", description = "Allowed to view the application team page")
    public boolean viewApplicationTeamPage(ApplicationCompositeId applicationCompositeId, UserResource loggedInUser) {
        return applicationNotYetSubmitted(applicationCompositeId.id());
    }
}
