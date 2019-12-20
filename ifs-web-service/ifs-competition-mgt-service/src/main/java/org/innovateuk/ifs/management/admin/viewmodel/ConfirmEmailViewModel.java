package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;


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

    public boolean doesUserNeedCrmUpdate() {
        return user.hasAnyRoles(APPLICANT, MONITORING_OFFICER);
    }
}
