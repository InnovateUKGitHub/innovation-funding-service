package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.Role;

import java.util.List;

import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static java.util.Arrays.asList;

/**
 * Holder of model attributes for the selection of role by a user
 */
public final class RoleSelectionViewModel {
    public static final List<Role> ACCEPTED_ROLES = asList(ASSESSOR, APPLICANT);

}
