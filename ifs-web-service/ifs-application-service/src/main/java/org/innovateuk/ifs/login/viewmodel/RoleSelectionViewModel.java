package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * Holder of model attributes for the selection of role by a user
 */
public final class RoleSelectionViewModel {

    private final List<Role> acceptedRoles;

    public static final String APPLICANT_ROLE_DESCRIPTION = "Manage your applications and projects.";
    public static final String ASSESSOR_ROLE_DESCRIPTION = "Review the applications you have been invited to assess.";
    public static final String STAKEHOLDER_ROLE_DESCRIPTION = "View the competitions you have been invited to oversee.";

    public RoleSelectionViewModel(UserResource user) {
        acceptedRoles = user.getRoles();
        getRoleDescription(user.getRolesString());
    }

    public List<Role> getAcceptedRoles() {
        return acceptedRoles;
    }

    public String getRoleDescription(String role){

        if (role.equals("Applicant")){
            return APPLICANT_ROLE_DESCRIPTION;
        } else if(role.equals("Assessor")){
            return ASSESSOR_ROLE_DESCRIPTION;
        } else if(role.equals("Stakeholder")){
            return STAKEHOLDER_ROLE_DESCRIPTION;
        } else return "";
    }

}
