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
    private static final Set<UserRoleType> ACCEPTED_ROLES = Sets.newHashSet(ASSESSOR, APPLICANT);

}
