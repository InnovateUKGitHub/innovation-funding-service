package com.worth.ifs.user.security;

import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.UserResource;
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
