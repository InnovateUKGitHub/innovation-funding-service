package com.worth.ifs.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;

/**
 *
 */
@Service
@Transactional
public class ServiceFailureTransactionRollbackAdvisorTestServiceImpl implements ServiceFailureTransactionRollbackAdvisorTestService {

    @Autowired
    public UserRepository userRepository;

    @Override
    public ServiceResult<String> successfulMethod() {

        User user = getUser();
        user.setName("Successful");
        userRepository.save(user);

        return serviceSuccess("Successful");
    }

    @Override
    public ServiceResult<String> failingMethod() {

        User user = getUser();
        user.setName("Failure");
        userRepository.save(user);

        return serviceFailure(notFoundError(User.class, "Failure"));
    }

    @Override
    public ServiceResult<String> exceptionThrowingMethod() {

        User user = getUser();
        user.setName("Exception");
        userRepository.save(user);

        throw new RuntimeException("Exception");
    }

    private User getUser() {
        return getOnlyElement(userRepository.findByEmail("steve.smith@empire.com"));
    }
}
