package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holder of model attributes for the selection of role by a user
 */
public final class RoleSelectionViewModel {

    private final List<Role> acceptedRoles;

    public RoleSelectionViewModel(UserResource user) {
        acceptedRoles = user.getRoles();
        roleDescription(user.getRolesString());
    }

    public List<Role> getAcceptedRoles() {
        return acceptedRoles;
    }

    public String roleDescription(String role) {

        String description ="";

        Map<String, String> roleDescription = new HashMap<>();

        roleDescription.put("Applicant", "Manage your applications and projects.");
        roleDescription.put("Assessor", "Review the applications you have been invited to assess.");
        roleDescription.put("Stakeholder", "View the competitions you have been invited to oversee.");

        if (role.equals("Applicant")) {
            description = roleDescription.get("Applicant");
        } else if (role.equals("Assessor")) {
            description = roleDescription.get("Assessor");
        } else if (role.equals("Stakeholder")) {
            description = roleDescription.get("Stakeholder");
        }

        return description;
    }
}
