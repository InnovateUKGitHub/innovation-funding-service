package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.user.domain.Role} data.
 */
public interface RoleService {

    @NotSecured(value = "Anyone can find roles by type", mustBeSecuredByOtherServices = false)
    ServiceResult<RoleResource> findByUserRoleType(UserRoleType userRoleType);

}
