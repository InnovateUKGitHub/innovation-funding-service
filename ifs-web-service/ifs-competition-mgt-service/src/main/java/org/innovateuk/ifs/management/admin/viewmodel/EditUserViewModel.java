package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.user.resource.UserResource;

/**
 * A view model for serving page listing users to be managed by IFS Administrators
 */
public class EditUserViewModel {

    private final UserResource user;
    private final boolean ifsAdmin;
    private final boolean editEmailFeatureToggle;

    public EditUserViewModel(UserResource user, boolean ifsAdmin, boolean editEmailFeatureToggle) {
        this.user = user;
        this.ifsAdmin = ifsAdmin;
        this.editEmailFeatureToggle = editEmailFeatureToggle;
    }

    public UserResource getUser() {
        return user;
    }

    public boolean isIfsAdmin() {
        return ifsAdmin;
    }

    public boolean isCanEditEmail() {
        return editEmailFeatureToggle && (ifsAdmin || user.isExternalUser());
    }

    public boolean isCanEditUserDetails() {
        return ifsAdmin && user.isInternalUser();
    }

    public boolean isInternal() {
        return user.isInternalUser();
    }

    public boolean isReadOnly() {
        return !ifsAdmin && (!editEmailFeatureToggle || user.isInternalUser());
    }
}
