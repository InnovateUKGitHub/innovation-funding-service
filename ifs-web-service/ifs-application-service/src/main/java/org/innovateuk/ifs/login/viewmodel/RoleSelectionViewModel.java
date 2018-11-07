package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * Holder of model attributes for the selection of role by a user
 */
public final class RoleSelectionViewModel {

    private final List<Role> acceptedRoles;

    public RoleSelectionViewModel(UserResource user) {
        acceptedRoles = user.getRoles();
    }

    public List<Role> getAcceptedRoles() {
        return acceptedRoles;
    }
}
