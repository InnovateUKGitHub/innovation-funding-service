package org.innovateuk.ifs.security;

import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LoggedInUserSupplier {

    @Autowired
    private UserMapper userMapper;

    public User get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResource user = (UserResource) authentication.getDetails();
        return userMapper.mapToDomain(user);
    }
}