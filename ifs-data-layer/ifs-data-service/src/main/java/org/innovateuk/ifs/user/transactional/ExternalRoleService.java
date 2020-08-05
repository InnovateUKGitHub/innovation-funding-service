package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ExternalRoleService {

    @SecuredBySpring(value = "ADD_USER_ROLE", description = "Only an admin can update a users role")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    ServiceResult<Void> addUserRole(long userId, Role role);

}
