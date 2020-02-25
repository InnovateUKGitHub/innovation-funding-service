package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationUserCompositeId;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ApplicationDeletionPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "DELETE_APPLICATION", description = "Only the lead can delete un-submitted applications")
    public boolean onlyApplicantCanDeleteUnsubmitted(final ApplicationResource applicationResource, UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user)
                && !applicationResource.isSubmitted();
    }

    @PermissionRule(value = "HIDE_APPLICATION", description = "Only the collaborators can hide applications")
    public boolean onlyCollaboratorsCanHideApplications(final ApplicationUserCompositeId id, UserResource user) {
        return isMemberOfProjectTeam(id.getApplicationId(), user)
                && user.getId().equals(id.getUserId());
    }


}

