package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * Holder of model attributes for the selection of role by a user
 */
public final class RoleSelectionViewModel {

    private final List<Role> acceptedRoles;

    static final String APPLICANT_ROLE_DESCRIPTION = "Manage your applications and projects.";
    static final String ASSESSOR_ROLE_DESCRIPTION = "Review the applications you have been invited to assess.";
    static final String STAKEHOLDER_ROLE_DESCRIPTION = "View the competitions you have been invited to oversee.";
    static final String EMPTY_DESCRIPTION = "";

    public RoleSelectionViewModel(UserResource user) {
        acceptedRoles = user.getRoles();
    }

    public List<Role> getAcceptedRoles() {
        return acceptedRoles;
    }

    public String getRoleDescription(Role role){

        if (role.equals(Role.APPLICANT)){
            return APPLICANT_ROLE_DESCRIPTION;
        } else if(role.equals(Role.ASSESSOR)){
            return ASSESSOR_ROLE_DESCRIPTION;
        } else if(role.equals(Role.STAKEHOLDER)){
            return STAKEHOLDER_ROLE_DESCRIPTION;
        } else return EMPTY_DESCRIPTION;
    }

}
