package org.innovateuk.ifs.user.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ExternalRoleServiceImpl implements ExternalRoleService {

    @Autowired
    private UserRepository userRepository;

    @Value("${ifs.system.external.user.email.domain}")
    private String externalUserEmailDomain;

    @Override
    @Transactional
    public ServiceResult<Void> addUserRole(long userId, Role role) {
        return find(userRepository.findById(userId), notFoundError(User.class, userId))
                .andOnSuccess(user -> validateRoleDoesNotExist(user, role))
                .andOnSuccess(user -> validateEmail(user))
                .andOnSuccessReturnVoid(user -> updateUserRole(user, role));
    }

    private ServiceResult<Void> updateUserRole(User user, Role role) {
        user.getRoles().add(role);
        return serviceSuccess();
    }

    private ServiceResult<User> validateRoleDoesNotExist(User user, Role role) {
        if(user.getRoles().contains(role)) {
         return serviceFailure(USER_ADD_ROLE_ROLE_ALREADY_EXISTS);
        }
        return serviceSuccess(user);
    }

    private ServiceResult<User> validateEmail(User user) {

        String domain = StringUtils.substringAfter(user.getEmail(), "@");

        if (!externalUserEmailDomain.equalsIgnoreCase(domain)) {
            return serviceFailure(USER_ADD_ROLE_INVALID_EMAIL);
        }

        return serviceSuccess(user);
    }
}
