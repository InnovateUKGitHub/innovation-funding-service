package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.domain.UserRejectionStatus;
import org.innovateuk.ifs.user.repository.UserRejectionStatusRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserRejection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;



@Service
public class UserRejectionStatusServiceImpl implements UserRejectionStatusService {

    @Autowired
    private UserRejectionStatusRepository userRejectionStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ServiceResult<Void> updateUserStatus(long userId, UserRejection userRejection) {
        return find(userRepository.findById(userId), notFoundError(User.class, userId)).andOnSuccess(user -> {
            UserRejectionStatus userRejectionStatus = new UserRejectionStatus(user, userRejection.getUserStatus(), userRejection.getReason());
            userRejectionStatusRepository.save(userRejectionStatus);
            return serviceSuccess();
        });
    }
}
