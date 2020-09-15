package org.innovateuk.ifs.user.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserControllerIntegrationTest extends BaseControllerIntegrationTest<UserController> {

    public static final String EMAIL = "steve.smith@empire.com";

    @Override
    @Autowired
    protected void setControllerUnderTest(UserController controller) {
        this.controller = controller;
    }

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${ifs.data.service.token.email.validity.mins}")
    private int emailTokenValidityMins;

    @Test
    public void findByEmailAddress() {
        loginSteveSmith();
        UserResource user = controller.findByEmail("steve.smith@empire.com").getSuccess();
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void findAll() {

        loginCompAdmin();
        List<UserResource> users = controller.findAll().getSuccess();
        assertEquals(USER_COUNT, users.size());

        //
        // Assert that we've got the users we were expecting
        //
        List<String> emailAddresses = users.stream().map(UserResource::getEmail).collect(toList());
        assertTrue(emailAddresses.containsAll(ALL_USERS_EMAIL));
    }

    @Test
    public void findByRole() {

        loginCompAdmin();
        List<UserResource> users = controller.findByRole(COMP_ADMIN).getSuccess();
        assertTrue(users.size() > 0);
    }

    @Test
    public void findByRoleAndUserStatus() {

        loginCompAdmin();
        List<UserResource> users = controller.findByRoleAndUserStatus(COMP_ADMIN, ACTIVE).getSuccess();
        assertTrue(users.size() > 0);
    }

    @Test
    public void sendPasswordResetNotification() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.sendPasswordResetNotification(EMAIL);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void checkPasswordResetToken() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.checkPasswordReset("a2e2928b-960f-469d-859f-f038b2bd9f42");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void sendPasswordResetNotificationInvalid() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.sendPasswordResetNotification("steveAAAAAsmith@empire.com");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void checkPasswordResetTokenInvalid() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.checkPasswordReset("a2e2928b-960f-INVALID-859f-f038b2bd9f42");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void verifyEmailInvalid() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.verifyEmail("4a5bc71c9f3a2bd50fada434d888====INVALID====650a739d1ad5b1a110614708d1fa083");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void verifyEmailExpired() {
        // save a token with a created date such that the token should have expired by now
        final String hash = "3514d94130e7959ad39e521554cd53eca4c4f6877740016af3e869c02869af16d4ccd85a53a62a3a";
        tokenRepository.save(new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, hash, now().minusMinutes(emailTokenValidityMins), JsonNodeFactory.instance.objectNode()));

        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.verifyEmail(hash);
        assertTrue(restResult.isFailure());
        assertTrue(restResult.getFailure().is(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED));
    }

    @Test
    public void updateUserDetailsInvalid() {
        UserResource user = new UserResource();
        user.setEmail("NotExistin@gUser.nl");
        user.setFirstName("Some");
        user.setLastName("How");

        RestResult<Void> restResult = controller.updateDetails(user);
        assertTrue(restResult.isFailure());
    }

    @Test
    public void updateUserDetails() {
        loginCompAdmin();
        UserResource userOne = controller.getUserById(1L).getSuccess();
        setLoggedInUser(userOne);

        userOne.setFirstName("Some");
        userOne.setLastName("How");

        setLoggedInUser(userOne);

        RestResult<Void> restResult = controller.updateDetails(userOne);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void resendEmailVerificationNotification() {
        loginSystemRegistrationUser();
        final RestResult<Void> restResult = controller.resendEmailVerificationNotification("ewan+1@hiveit.co.uk");
        assertTrue(restResult.isSuccess());
    }
}
