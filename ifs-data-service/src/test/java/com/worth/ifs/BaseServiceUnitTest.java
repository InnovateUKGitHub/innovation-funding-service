package com.worth.ifs;

import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.user.resource.UserResource;
import org.mockito.InjectMocks;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This is the base class for testing Services with mock components.
 *
 */
public abstract class BaseServiceUnitTest<ServiceType> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected ServiceType service = supplyServiceUnderTest();

    protected abstract ServiceType supplyServiceUnderTest();

    protected void setLoggedInUser(UserResource loggedInUser) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(loggedInUser));
    }
}