package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * A test Service for tests in {@link ServiceFailureExceptionHandlingAdviceTest}
 */
@Service
@Transactional
public class ServiceFailureExceptionHandlingAdviceTestServiceImpl extends BaseTransactionalService implements ServiceFailureExceptionHandlingAdviceTestService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceFailureExceptionHandlingAdviceInternalTestService internalTestService;

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
    public ServiceResult<String> successfulMethodWithInternalFailingCall() {

        User user = getUser();
        user.setFirstName("Successful Internal");
        userRepository.save(user);

        assert internalTestService.internalFailingCall().isFailure();

        return serviceSuccess("Successful");
    }

    @Override
    public ServiceResult<String> failingMethodWithInternalFailingCall() {

        User user = getUser();
        user.setFirstName("Successful Internal");
        userRepository.save(user);

        return internalTestService.internalFailingCall();
    }

    @Override
    public ServiceResult<String> accessDeniedMethod() {
        return null;
    }

    private User getUser() {
        return userRepository.findByEmail("steve.smith@empire.com").get();
    }
}
