package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserRoleType;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.user.domain.Role} data.
 */
public interface RoleService {

    @NotSecured(value = "Anyone can find roles by type", mustBeSecuredByOtherServices = false)
    ServiceResult<RoleResource> findByUserRoleType(UserRoleType userRoleType);

}