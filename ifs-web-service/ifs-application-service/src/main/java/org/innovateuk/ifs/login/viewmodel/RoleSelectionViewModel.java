package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.Role;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;

/**
 * Holder of model attributes for the selection of role by a user
 */
public final class RoleSelectionViewModel {
    public static final List<Role> ACCEPTED_ROLES = unmodifiableList(asList(ASSESSOR, APPLICANT));

    public RoleSelectionViewModel() {
        // no-arg constructor
    }

}
