package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.LambdaMatcher;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.Optional;

import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
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

        UserResource userToUpdate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        UserResource existingUser = newUserResource().build();
        UserResource updatedUser = newUserResource().build();

        when(userServiceMock.findByEmail("email@example.com")).thenReturn(serviceSuccess(existingUser));

        LambdaMatcher<User> expectedUserMatcher = lambdaMatches(user -> {

            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("Mr", user.getTitle());

            return true;
        });
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
