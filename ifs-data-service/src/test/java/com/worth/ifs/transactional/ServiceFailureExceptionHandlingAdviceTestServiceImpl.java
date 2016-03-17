package com.worth.ifs.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * A test Service for tests in {@link ServiceFailureExceptionHandlingAdviceTest}
 */
@Service
public class ServiceFailureExceptionHandlingAdviceTestServiceImpl extends BaseTransactionalService implements ServiceFailureExceptionHandlingAdviceTestService {

    @Autowired
    public UserRepository userRepository;

    @Override
    public ServiceResult<String> successfulMethod() {

        User user = getUser();
        user.setFirstName("Successful");
        userRepository.save(user);

        return serviceSuccess("Successful");
    }

    @Override
    public ServiceResult<String> restoreSuccessfulMethod() {

        User user = getUser();
        user.setFirstName("Steve");
        userRepository.save(user);

        return serviceSuccess("Successful restore");
    }

    @Override
    public ServiceResult<String> failingMethod() {

        User user = getUser();
        user.setFirstName("Failure");
        userRepository.save(user);

        return serviceFailure(notFoundError(User.class, "Failure"));
    }

    @Override
    public ServiceResult<String> exceptionThrowingMethod() {

        User user = getUser();
        user.setFirstName("Exception");
        userRepository.save(user);

        throw new RuntimeException("Exception");
    }

    @Override
    public ServiceResult<String> accessDeniedMethod() {
        return null;
    }

    private User getUser() {
        return userRepository.findByEmail("steve.smith@empire.com").get();
    }
}
