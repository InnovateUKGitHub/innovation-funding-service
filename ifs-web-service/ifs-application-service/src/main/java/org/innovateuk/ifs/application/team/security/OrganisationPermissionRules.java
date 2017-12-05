package org.innovateuk.ifs.application.team.security;

import org.innovateuk.ifs.application.resource.ApplicationCompositeId;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Permission checker around the access to Organisation
 */
@PermissionRules
@Component
public class OrganisationPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "VIEW_ADD_ORGANISATION_PAGE", description = "Allowed to view the add organisation page")
    public boolean viewAddOrganisationPage(ApplicationCompositeId applicationCompositeId, UserResource loggedInUser) {
        return isLeadApplicant(applicationCompositeId.id(), loggedInUser) && applicationNotYetSubmitted(applicationCompositeId.id());
    }

    @PermissionRule(value = "ADD_NEW_ORGANISATION", description = "Allowed to add a new organisation")
    public boolean addNewOrganisation(ApplicationCompositeId applicationCompositeId, UserResource loggedInUser) {
        return isLeadApplicant(applicationCompositeId.id(), loggedInUser) && applicationNotYetSubmitted(applicationCompositeId.id());
    }
}
