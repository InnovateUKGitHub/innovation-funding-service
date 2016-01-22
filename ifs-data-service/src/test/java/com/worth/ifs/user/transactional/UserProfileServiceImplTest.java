package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.LambdaMatcher;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.USER_NOT_FOUND;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.transactional.UserProfileServiceImpl.ServiceFailures.UNABLE_TO_UPDATE_USER;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
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

        User existingUser = newUser().build();
        User updatedUser = newUser().build();

        when(userRepositoryMock.findByEmail("email@example.com")).thenReturn(singletonList(existingUser));

        LambdaMatcher<User> expectedUserMatcher = lambdaMatches(user -> {

            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("Mr", user.getTitle());

            return true;
        });

        when(userRepositoryMock.save(argThat(expectedUserMatcher))).thenReturn(updatedUser);

        ServiceResult<User> result = service.updateProfile(userToUpdate);
        assertTrue(result.isRight());
        assertEquals(updatedUser, result.getRight());
    }

    @Test
    public void testUpdateProfileButUserNotFound() {

        UserResource userToUpdate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        when(userRepositoryMock.findByEmail("email@example.com")).thenReturn(emptyList());

        ServiceResult<User> result = service.updateProfile(userToUpdate);
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(USER_NOT_FOUND));
    }


    @Test
    public void testUpdateProfileButThrowsException() {

        UserResource userToUpdate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        User existingUser = newUser().build();

        when(userRepositoryMock.findByEmail("email@example.com")).thenReturn(singletonList(existingUser));
        when(userRepositoryMock.save(isA(User.class))).thenThrow(new IllegalArgumentException("no updating!"));

        ServiceResult<User> result = service.updateProfile(userToUpdate);
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_UPDATE_USER));
    }
}
