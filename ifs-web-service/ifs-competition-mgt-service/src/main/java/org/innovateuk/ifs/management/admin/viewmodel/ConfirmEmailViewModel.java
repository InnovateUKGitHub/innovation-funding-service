package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.user.resource.UserProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;


public class ConfirmEmailViewModel {

    private final UserResource user;
    private final String email;
    private final String organisation;
    private final UserProfileResource userProfileResource;

    public ConfirmEmailViewModel(UserResource user, String email, String organisation, UserProfileResource userProfileResource) {
        this.user = user;
        this.email = email;
        this.organisation = organisation;
        this.userProfileResource = userProfileResource;
    }

    public UserResource getUser() {
        return user;
    }

    public String getEmail() {
        return email;
    }

    public String getOrganisation() {
        return organisation;
    }

    public UserProfileResource getUserProfileResource() {
        return userProfileResource;
    }

    public boolean doesUserNeedCrmUpdate() {
        return user.hasAnyRoles(APPLICANT, MONITORING_OFFICER);
    }

    public boolean isChangingOrgName() {
        return !this.organisation.isEmpty() && !this.organisation.equals(userProfileResource.getSimpleOrganisation());
    }
}
