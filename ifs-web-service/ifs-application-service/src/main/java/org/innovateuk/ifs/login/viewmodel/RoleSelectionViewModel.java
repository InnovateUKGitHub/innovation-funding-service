package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.UserRoleType;

import java.util.List;

import static org.innovateuk.ifs.user.resource.UserRoleType.APPLICANT;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static java.util.Arrays.asList;

/**
 * Holder of model attributes for the selection of role by a user
 */
public class RoleSelectionViewModel {
    public static final List<UserRoleType> ACCEPTED_ROLES = asList(ASSESSOR, APPLICANT);
}
