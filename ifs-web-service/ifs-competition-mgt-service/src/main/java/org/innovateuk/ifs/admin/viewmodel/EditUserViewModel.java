package org.innovateuk.ifs.admin.viewmodel;

import org.innovateuk.ifs.user.resource.UserResource;

/**
 * A view model for serving page listing users to be managed by IFS Administrators
 */
public class EditUserViewModel {
    private UserResource user;

    public EditUserViewModel(UserResource user) {
        this.user = user;
    }

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }
}
