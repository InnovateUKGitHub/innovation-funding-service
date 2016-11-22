package com.worth.ifs.login.viewmodel;

import com.worth.ifs.user.resource.UserRoleType;

import java.util.List;

import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static java.util.Arrays.asList;

/**
 * Holder of model attributes for the selection of role by a user
 */
public class RoleSelectionViewModel {
    public static final List<UserRoleType> ACCEPTED_ROLES = asList(ASSESSOR, APPLICANT);
}
