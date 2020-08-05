package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.Role;

public interface ExternalRoleRestService {
    RestResult<Void> addRoleToUser(long userId, Role role);
}
