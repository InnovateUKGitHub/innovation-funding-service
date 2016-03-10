package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserStatus;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Tests for {@link UserRestServiceImpl}.
 */
public class UserRestServiceIntegrationTest extends BaseRestServiceIntegrationTest<UserRestService> {

    public static final String EMAIL = "steve.smith@empire.com";

//    @Autowired
//    TokenRepository tokenRepository;

    @Override
    @Autowired
    protected void setRestService(UserRestService service) {
        this.service = service;
    }

    @Test
    public void test_retrieveUserByToken() {
        User user = service.retrieveUserByToken("123abc").getSuccessObject();
        assertNotNull(user);
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void test_retrieveUserByEmailAndPassword() {
        User user = service.retrieveUserByEmailAndPassword(EMAIL, "test").getSuccessObject();
        assertNotNull(user);
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void test_retrieveUserByEmailAndPasswordInvalid() {
        assertTrue(service.retrieveUserByEmailAndPassword("", "test").isFailure());
    }

    @Test
    public void testSendPasswordResetNotification() {
        RestResult<Void> restResult = service.sendPasswordResetNotification(EMAIL);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testSendPasswordResetNotificationInvalid() {
        RestResult<Void> restResult = service.sendPasswordResetNotification("steveAAAAAsmith@empire.com");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void testCheckPasswordResetTokenInvalid() {
        RestResult<Void> restResult = service.checkPasswordResetHash("a2e2928b-960f-INVALID-859f-f038b2bd9f42");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void testCheckPasswordResetToken() {
        RestResult<Void> restResult = service.checkPasswordResetHash("a2e2928b-960f-469d-859f-f038b2bd9f42");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testPasswordReset() {
        RestResult<User> userInvalid = service.retrieveUserByEmailAndPassword(EMAIL, "INVALID");
        assertTrue(userInvalid.isFailure());
        userInvalid = service.retrieveUserByEmailAndPassword(EMAIL, "newPassword");
        assertTrue(userInvalid.isFailure());

        RestResult<User> userBefore = service.retrieveUserByEmailAndPassword(EMAIL, "test");
        assertTrue(userBefore.isSuccess());
        assertNotNull(userBefore.getSuccessObject());
        assertEquals(EMAIL, userBefore.getSuccessObject().getEmail());

        RestResult<Void> restResult = service.resetPassword("a2e2928b-960f-469d-859f-f038b2bd9f42", "newPassword");
        assertTrue(restResult.isSuccess());

        RestResult<User> userAfter = service.retrieveUserByEmailAndPassword(EMAIL, "newPassword");
        assertTrue(userAfter.isSuccess());
    }


    @Test
    public void testVerifyEmail() {
        RestResult<User> beforeVerify = service.retrieveUserByEmailAndPassword("ewan+12@hiveit.co.uk", "testtest");
        assertTrue(beforeVerify.isFailure());

        RestResult<Void> restResult = service.verifyEmail("4a5bc71c9f3a2bd50fada434d888579aec0bd53fe7b3ca3fc650a739d1ad5b1a110614708d1fa083");
        assertTrue(restResult.isSuccess());

        RestResult<User> afterVerify = service.retrieveUserByEmailAndPassword("ewan+12@hiveit.co.uk", "testtest");
        assertTrue(afterVerify.isSuccess());
    }

    @Test
    public void testVerifyEmailInvalid() {
        RestResult<Void> restResult = service.verifyEmail("4a5bc71c9f3a2bd50fada434d888====INVALID====650a739d1ad5b1a110614708d1fa083");
        assertTrue(restResult.isFailure());
    }


    @Test
    public void testCreateLeadApplicant() {
        String firstName = "Some";
        String lastName = "How";
        String password = "Password123";
        String email = "email@Nope.com";
        String title = "King";
        String phoneNumber = "0123335787888";

        RestResult<UserResource> restResult = service.createLeadApplicantForOrganisation(firstName, lastName, password, email, title, phoneNumber, 1L);
        assertTrue(restResult.isSuccess());

        UserResource user = restResult.getSuccessObject();
        assertEquals(firstName, user.getFirstName());
        assertEquals(email, user.getEmail());
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }

    // for this to work we might need something like explained here:
    // http://docs.spring.io/spring-security/site/docs/4.0.x/reference/htmlsingle/#test-method-withsecuritycontext
    @Ignore("We need authentication to change a users profile.")
    @Test
    public void testUpdateUserDetailsInvalid() {
        String firstName = "Some";
        String lastName = "How";
        String title = "King";
        String phoneNumber = "0123335787888";

        RestResult<UserResource> restResult = service.updateDetails("NotExistin@gUser.nl", firstName, lastName, title, phoneNumber);
        assertTrue(restResult.isFailure());
    }


    @Ignore("We need authentication to change a users profile.")
    @Test
    public void testUpdateUserDetails() {
        String firstName = "Some";
        String lastName = "How";
        String title = "King";
        String phoneNumber = "0123335787888";

        RestResult<UserResource> restResult = service.updateDetails(EMAIL, firstName, lastName, title, phoneNumber);
        assertTrue(restResult.isSuccess());
    }
}
