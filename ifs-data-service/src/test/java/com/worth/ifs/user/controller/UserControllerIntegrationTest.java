package com.worth.ifs.user.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserStatus;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

/**
 *
 * Integration tests for {@link UserController}.
 *
 * Created by dwatson on 02/10/15.
 */
public class UserControllerIntegrationTest extends BaseControllerIntegrationTest<UserController> {

    public static final String EMAIL = "steve.smith@empire.com";

    @Override
    @Autowired
    protected void setControllerUnderTest(UserController controller) {
        this.controller = controller;
    }

    @Test
    public void test_findByEmailAddress() {
        UserResource user= controller.findByEmail("steve.smith@empire.com").getSuccessObject();
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void test_findAll() {

        List<User> users = controller.findAll().getSuccessObject();
        assertEquals(USER_COUNT, users.size());

        //
        // Assert that we've got the users we were expecting
        //
        List<String> emailAddresses = users.stream().map(User::getEmail).collect(toList());
        List<String> expectedUsers = ALL_USERS_EMAIL;
        assertTrue(emailAddresses.containsAll(expectedUsers));
    }

    @Test
    public void testSendPasswordResetNotification() {
        RestResult<Void> restResult = controller.sendPasswordResetNotification(EMAIL);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testCheckPasswordResetToken() {
        RestResult<Void> restResult = controller.checkPasswordReset("a2e2928b-960f-469d-859f-f038b2bd9f42");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testSendPasswordResetNotificationInvalid() {
        RestResult<Void> restResult = controller.sendPasswordResetNotification("steveAAAAAsmith@empire.com");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void testCheckPasswordResetTokenInvalid() {
        RestResult<Void> restResult = controller.checkPasswordReset("a2e2928b-960f-INVALID-859f-f038b2bd9f42");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void test_retrieveUserByEmailAndPasswordInvalid() {
        assertTrue(controller.getUserByEmailandPassword("", "test").isFailure());
    }

    @Test
    public void testVerifyEmail() {
        RestResult<User> beforeVerify = controller.getUserByEmailandPassword("ewan+12@hiveit.co.uk", "testtest");
        assertTrue(beforeVerify.isFailure());

        RestResult<Void> restResult = controller.verifyEmail("4a5bc71c9f3a2bd50fada434d888579aec0bd53fe7b3ca3fc650a739d1ad5b1a110614708d1fa083");
        assertTrue(restResult.isSuccess());

        RestResult<User> afterVerify = controller.getUserByEmailandPassword("ewan+12@hiveit.co.uk", "testtest");
        assertTrue(afterVerify.isSuccess());
    }

    @Test
    public void testVerifyEmailInvalid() {
        RestResult<Void> restResult = controller.verifyEmail("4a5bc71c9f3a2bd50fada434d888====INVALID====650a739d1ad5b1a110614708d1fa083");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void testPasswordReset() {
        RestResult<User> userInvalid = controller.getUserByEmailandPassword(EMAIL, "INVALID");
        assertTrue(userInvalid.isFailure());
        userInvalid = controller.getUserByEmailandPassword(EMAIL, "newPassword");
        assertTrue(userInvalid.isFailure());

        RestResult<User> userBefore = controller.getUserByEmailandPassword(EMAIL, "test");
        assertTrue(userBefore.isSuccess());
        assertNotNull(userBefore.getSuccessObject());
        assertEquals(EMAIL, userBefore.getSuccessObject().getEmail());

        RestResult<Void> restResult = controller.resetPassword("a2e2928b-960f-469d-859f-f038b2bd9f42", "newPassword");
        assertTrue(restResult.isSuccess());

        RestResult<User> userAfter = controller.getUserByEmailandPassword(EMAIL, "newPassword");
        assertTrue(userAfter.isSuccess());
    }

    @Test
    public void testUpdateUserDetailsInvalid() {
        UserResource user = new UserResource();
        user.setEmail("NotExistin@gUser.nl");
        user.setFirstName("Some");
        user.setLastName("How");

        RestResult<UserResource> restResult = controller.createUser(user);
        assertTrue(restResult.isFailure());
    }

    @Test
    public void testUpdateUserDetails() {
        User userOne = controller.getUserById(1L).getSuccessObject();

        UserResource userResource = new UserResource(userOne);
        userResource.setFirstName("Some");
        userResource.setLastName("How");

        RestResult<UserResource> restResult = controller.createUser(userResource);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testCreateLeadApplicant() {
        UserResource userResource = new UserResource();
        userResource.setFirstName("Some");
        userResource.setLastName("How");
        userResource.setPassword("Password123");
        userResource.setEmail("email@Nope.com");
        userResource.setTitle("King");
        userResource.setPhoneNumber("0123335787888");

        RestResult<UserResource> restResult = controller.createUser(1L, 1L, userResource);
        assertTrue(restResult.isSuccess());

        UserResource user = restResult.getSuccessObject();
        assertEquals("email@Nope.com", user.getEmail());
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }
}
