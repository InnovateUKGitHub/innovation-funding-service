package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.UserTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * A Service that covers basic operations concerning Users
 */
@Service
public class BaseUserServiceImpl extends UserTransactionalService implements BaseUserService {

    enum Notifications {
        VERIFY_EMAIL_ADDRESS,
        RESET_PASSWORD
    }

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServiceResult<UserResource> getUserResourceByUid(final String uid) {
        return find(userRepository.findOneByUid(uid), notFoundError(UserResource.class, uid)).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<UserResource> getUserById(final Long id) {
        return super.getUser(id).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<UserResource>> findAll() {
        return serviceSuccess(usersToResources(userRepository.findAll()));
    }

    @Override
    public ServiceResult<List<UserResource>> findByProcessRole(UserRoleType roleType) {
        return serviceSuccess(usersToResources(userRepository.findByRolesName(roleType.getName())));
    }

    private List<UserResource> usersToResources(List<User> filtered) {
        return simpleMap(filtered, user -> userMapper.mapToResource(user));
    }
}
