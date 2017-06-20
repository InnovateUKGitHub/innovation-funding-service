package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that is able to look up UserResources for the permission checker
 */
@Component
@PermissionEntityLookupStrategies
public class UserLookupStrategies {

    @Autowired
    private UserMapper userMapper;

    @PermissionEntityLookupStrategy
    public UserResource findById(Long id) {
        return userMapper.mapIdToResource(id);
    }
}
