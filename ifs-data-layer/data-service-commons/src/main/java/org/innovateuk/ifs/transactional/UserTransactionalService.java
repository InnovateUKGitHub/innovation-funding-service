package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * This class represents the base class for user transactional services.
 * Method calls within this service will have transaction boundaries
 * provided to allow for safe atomic operations and persistence cascading.
 */
@Transactional
public abstract class UserTransactionalService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    protected Supplier<ServiceResult<User>> user(final Long id) {
        return () -> getUser(id);
    }

    protected ServiceResult<User> getUser(final Long id) {
        return find(userRepository.findOne(id), notFoundError(User.class, id));
    }

    protected Supplier<ServiceResult<Role>> role(UserRoleType roleType) {
        return () -> getRole(roleType);
    }

    protected Supplier<ServiceResult<Role>> role(String roleName) {
        return () -> getRole(roleName);
    }

    protected ServiceResult<Role> getRole(UserRoleType roleType) {
        return getRole(roleType.getName());
    }

    protected ServiceResult<Role> getRole(String roleName) {
        return find(roleRepository.findOneByName(roleName), notFoundError(Role.class, roleName));
    }

    protected ServiceResult<User> getCurrentlyLoggedInUser() {
        UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();

        if (currentUser == null) {
            return serviceFailure(forbiddenError());
        }

        return getUser(currentUser.getId());
    }
}
