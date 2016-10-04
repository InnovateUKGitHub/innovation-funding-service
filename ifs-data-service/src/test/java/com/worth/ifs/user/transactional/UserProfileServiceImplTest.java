package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.LambdaMatcher;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.Optional;

import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.ProfileResourceBuilder.newProfileResource;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

/**
 * Tests around the User Profile Service
 */
public class UserProfileServiceImplTest extends BaseServiceUnitTest<UserProfileServiceImpl> {

    @Override
    protected UserProfileServiceImpl supplyServiceUnderTest() {
        return new UserProfileServiceImpl();
    }

    @Test
    public void testUpdateProfile() {

        ProfileResource profile = newProfileResource().build();

        UserResource userToUpdate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                withProfile(profile).
                build();

        UserResource existingUser = newUserResource().build();

        when(userServiceMock.findByEmail("email@example.com")).thenReturn(serviceSuccess(existingUser));

        ServiceResult<Void> result = service.updateProfile(userToUpdate);
        assertTrue(result.isSuccess());

    }

    @Test
    public void testUpdateProfileButUserNotFound() {

        UserResource userToUpdate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@serviceFailureexample.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        when(userServiceMock.findByEmail(userToUpdate.getEmail())).thenReturn(serviceFailure(notFoundError(User.class, userToUpdate.getEmail())));

        ServiceResult<Void> result = service.updateProfile(userToUpdate);
        assertTrue(result.isFailure());
    }
}
