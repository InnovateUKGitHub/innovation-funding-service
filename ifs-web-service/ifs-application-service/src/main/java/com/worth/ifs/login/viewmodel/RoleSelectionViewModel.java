package com.worth.ifs.login.viewmodel;

import com.google.common.collect.Sets;
import com.worth.ifs.user.resource.UserRoleType;

import java.util.Set;

import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;

/**
 * Holder of model attributes for the selection of role by a user
 */
public class RoleSelectionViewModel {
    private Set<UserRoleType> acceptedRoles = Sets.newHashSet(ASSESSOR, APPLICANT);

    public Set<UserRoleType> getAcceptedRoles() {
        return acceptedRoles;
    }
}
