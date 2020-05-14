package org.innovateuk.ifs.util;


import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;


/**
 * A util class to provide common authentication related services
 */
@Component
public class AuthenticationHelper {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private ServiceResult<User> getUser(final Long id) {
        return find(userRepository.findById(id), notFoundError(User.class, id));
    }

    public ServiceResult<User> getCurrentlyLoggedInUser() {
        UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();

        if (currentUser == null) {
            return serviceFailure(forbiddenError());
        }

        return getUser(currentUser.getId());
    }

    @Transactional(readOnly = true)
    public void loginSystemUser() {
        UserResource user = userMapper.mapToResource(userRepository.findByRoles(Role.SYSTEM_MAINTAINER).get(0));
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
    }
}
