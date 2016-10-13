package com.worth.ifs.user.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.token.resource.TokenType;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.*;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED;
import static com.worth.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.ProfileBuilder.newProfile;
import static com.worth.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static com.worth.ifs.user.resource.AffiliationType.*;
import static com.worth.ifs.user.resource.BusinessType.BUSINESS;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${ifs.data.service.token.email.validity.mins}")
    private int emailTokenValidityMins;

    @Test
    public void test_findByEmailAddress() {
        loginSteveSmith();
        UserResource user= controller.findByEmail("steve.smith@empire.com").getSuccessObject();
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void test_findAll() {

        loginCompAdmin();
        List<UserResource> users = controller.findAll().getSuccessObject();
        assertEquals(USER_COUNT, users.size());

        //
        // Assert that we've got the users we were expecting
        //
        List<String> emailAddresses = users.stream().map(UserResource::getEmail).collect(toList());
        List<String> expectedUsers = ALL_USERS_EMAIL;
        assertTrue(emailAddresses.containsAll(expectedUsers));
    }

    @Test
    public void test_findByRole() {

        loginCompAdmin();
        List<UserResource> users = controller.findByRole(UserRoleType.COMP_EXEC.getName()).getSuccessObject();
        int compExecUserCount = 2;
        assertEquals(compExecUserCount, users.size());
    }

    @Test
    public void testSendPasswordResetNotification() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.sendPasswordResetNotification(EMAIL);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testCheckPasswordResetToken() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.checkPasswordReset("a2e2928b-960f-469d-859f-f038b2bd9f42");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testSendPasswordResetNotificationInvalid() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.sendPasswordResetNotification("steveAAAAAsmith@empire.com");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void testCheckPasswordResetTokenInvalid() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.checkPasswordReset("a2e2928b-960f-INVALID-859f-f038b2bd9f42");
        assertTrue(restResult.isFailure());
    }

    @Ignore("TODO DW - INFUND-936 - Not valid test after passwords moved out to Shib")
    @Test
    public void testVerifyEmail() {
        RestResult<UserResource> beforeVerify = controller.getUserByUid("6198a6e1-495f-402e-9eff-28611efeadb8");
        assertTrue(beforeVerify.isFailure());

        RestResult<Void> restResult = controller.verifyEmail("4a5bc71c9f3a2bd50fada434d888579aec0bd53fe7b3ca3fc650a739d1ad5b1a110614708d1fa083");
        assertTrue(restResult.isSuccess());

        RestResult<UserResource> afterVerify = controller.getUserByUid("6198a6e1-495f-402e-9eff-28611efeadb8");
        assertTrue(afterVerify.isSuccess());
    }

    @Test
    public void testVerifyEmailInvalid() {
        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.verifyEmail("4a5bc71c9f3a2bd50fada434d888====INVALID====650a739d1ad5b1a110614708d1fa083");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void testVerifyEmailExpired() {
        // save a token with a created date such that the token should have expired by now
        final String hash = "3514d94130e7959ad39e521554cd53eca4c4f6877740016af3e869c02869af16d4ccd85a53a62a3a";
        tokenRepository.save(new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, hash, LocalDateTime.now().minusMinutes(emailTokenValidityMins), JsonNodeFactory.instance.objectNode()));

        loginSystemRegistrationUser();
        RestResult<Void> restResult = controller.verifyEmail(hash);
        assertTrue(restResult.isFailure());
        assertTrue(restResult.getFailure().is(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED));
    }

    @Ignore("TODO DW - INFUND-936 - Not valid test after passwords moved out to Shib")
    @Test
    public void testPasswordReset() {
        RestResult<Void> restResult = controller.resetPassword("a2e2928b-960f-469d-859f-f038b2bd9f42", "newPasswsadf0rd");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testUpdateUserDetailsInvalid() {
        UserResource user = new UserResource();
        user.setEmail("NotExistin@gUser.nl");
        user.setFirstName("Some");
        user.setLastName("How");

        RestResult<Void> restResult = controller.updateDetails(user);
        assertTrue(restResult.isFailure());
    }

    @Test
    public void testUpdateUserDetails() {
        loginCompAdmin();
        UserResource userOne = controller.getUserById(1L).getSuccessObject();
        setLoggedInUser(userOne);

        userOne.setFirstName("Some");
        userOne.setLastName("How");

        setLoggedInUser(userOne);

        RestResult<Void> restResult = controller.updateDetails(userOne);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testGetProfileSkills() {
        loginPaulPlum();

        User user = userRepository.findOne(getPaulPlum().getId());
        Long userId = user.getId();

        user.setProfile(newProfile()
                .with(id(null))
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Skills")
                .build());
        userRepository.save(user);

        ProfileSkillsResource response = controller.getProfileSkills(userId).getSuccessObjectOrThrowException();
        assertEquals(userId, response.getUser());
        assertEquals(BUSINESS, response.getBusinessType());
        assertEquals("Skills", response.getSkillsAreas());
    }

    @Test
    public void testUpdateProfileSkills() {
        loginCompAdmin();
        UserResource userOne = controller.getUserById(1L).getSuccessObject();
        setLoggedInUser(userOne);

        ProfileSkillsResource profileSkills = newProfileSkillsResource()
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Skills")
                .build();

        RestResult<Void> restResult = controller.updateProfileSkills(1L, profileSkills);
        assertTrue(restResult.isSuccess());
    }

    @Ignore("TODO DW - INFUND-936 - this test will cause issues when not running Shib or on an environment like Bamboo where no Shib is available")
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

    @Test
    public void testResendEmailVerificationNotification() {
        loginSystemRegistrationUser();
        final RestResult<Void> restResult = controller.resendEmailVerificationNotification("ewan+1@hiveit.co.uk");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testGetUserAffiliations() throws Exception {
        loginPaulPlum();

        User user = userRepository.findOne(getPaulPlum().getId());
        Long userId = user.getId();

        // Save some existing Affiliations
        user.setAffiliations(newAffiliation()
                .withId(null, null)
                .withAffiliationType(EMPLOYER, PERSONAL_FINANCIAL)
                .withExists(TRUE, TRUE)
                .withUser(user, user)
                .build(2));
        userRepository.save(user);

        List<AffiliationResource> response = controller.getUserAffiliations(userId).getSuccessObjectOrThrowException();
        assertEquals(2, response.size());

        assertEquals(EMPLOYER, response.get(0).getAffiliationType());
        assertEquals(PERSONAL_FINANCIAL, response.get(1).getAffiliationType());
    }

    @Test
    public void testUpdateUserAffiliations() throws Exception {
        loginPaulPlum();

        User user = userRepository.findOne(getPaulPlum().getId());
        Long userId = user.getId();

        // Save some existing Affiliations
        user.setAffiliations(newAffiliation()
                .withId(null, null)
                .withAffiliationType(EMPLOYER, PERSONAL_FINANCIAL)
                .withExists(TRUE, TRUE)
                .withUser(user, user)
                .build(2));
        userRepository.save(user);

        List<AffiliationResource> getAfterSaveResponse = controller.getUserAffiliations(userId).getSuccessObjectOrThrowException();
        assertEquals(2, getAfterSaveResponse.size());

        RestResult<Void> updateResponse = controller.updateUserAffiliations(userId, newAffiliationResource()
                .withId(null, null)
                .withAffiliationType(PROFESSIONAL, FAMILY_FINANCIAL)
                .withExists(TRUE, TRUE)
                .withUser(userId, userId)
                .build(2));

        assertTrue(updateResponse.isSuccess());

        List<AffiliationResource> getAfterUpdateResponse = controller.getUserAffiliations(userId).getSuccessObjectOrThrowException();
        assertEquals(2, getAfterUpdateResponse.size());
        assertEquals(PROFESSIONAL, getAfterUpdateResponse.get(0).getAffiliationType());
        assertEquals(FAMILY_FINANCIAL, getAfterUpdateResponse.get(1).getAffiliationType());
    }
}
