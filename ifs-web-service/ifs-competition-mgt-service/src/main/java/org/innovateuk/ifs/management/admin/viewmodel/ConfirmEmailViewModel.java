package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;


public class ConfirmEmailViewModel {

    private final UserResource user;
    private final String email;

    public ConfirmEmailViewModel(UserResource user, String email) {
        this.user = user;
        this.email = email;
    }

    public UserResource getUser() {
        return user;
    }

    public String getEmail() {
        return email;
    }

    public boolean isUserHasLiveProjectRole() {
        return user.hasRole(Role.LIVE_PROJECTS_USER);
    }
}
