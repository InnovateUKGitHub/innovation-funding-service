package com.worth.ifs.user.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class UserServiceImplTest extends BaseServiceUnitTest<UserService> {

    private static final String EMAIL_THAT_EXISTS_FOR_USER = "sample@me.com";

    @Mock
    private UserRestService userRestService;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        when(userRestService.sendEmailVerificationNotification(EMAIL_THAT_EXISTS_FOR_USER)).thenReturn(restSuccess());
        when(userRestService.sendEmailVerificationNotification(not(eq(EMAIL_THAT_EXISTS_FOR_USER)))).thenReturn(restFailure(notFoundError(UserResource.class)));
    }

    @Override
    protected UserService supplyServiceUnderTest() {
        return new UserServiceImpl();
    }

    @Test
    public void sendEmailVerificationNotification() throws Exception {
        final String email = "sample@me.com";

        service.sendEmailVerificationNotification(email);
        verify(userRestService, only()).sendEmailVerificationNotification(email);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void sendEmailVerificationNotification_notExists() throws Exception {
        // try sending the verification link to any email except the one which is known to exist for a user
        final String email = "i-dont-exist@me.com";

        service.sendEmailVerificationNotification(email);
        verify(userRestService, only()).sendEmailVerificationNotification(email);
    }
}