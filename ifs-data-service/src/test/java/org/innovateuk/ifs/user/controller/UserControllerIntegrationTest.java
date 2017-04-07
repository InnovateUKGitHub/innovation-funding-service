package org.innovateuk.ifs.user.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.AgreementRepository;
import org.innovateuk.ifs.user.repository.ProfileRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.AgreementBuilder.newAgreement;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for {@link UserController}.
 * <p>
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
    private AgreementRepository agreementRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${ifs.data.service.token.email.validity.mins}")
    private int emailTokenValidityMins;

    @Test
    public void test_findByEmailAddress() {
        loginSteveSmith();
        UserResource user = controller.findByEmail("steve.smith@empire.com").getSuccessObject();
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
        List<UserResource> users = controller.findByRole(UserRoleType.COMP_ADMIN.getName()).getSuccessObject();
        assertTrue(users.size() > 0);
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
        tokenRepository.save(new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, hash, now().minusMinutes(emailTokenValidityMins), JsonNodeFactory.instance.objectNode()));

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

        InnovationArea innovationArea = innovationAreaRepository.save(newInnovationArea()
                .with(id(null))
                .withName("Innovation area")
                .build());

        Profile profile = profileRepository.save(newProfile()
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Skills")
                .withInnovationAreas(singletonList(innovationArea))
                .build());

        user.setProfileId(profile.getId());
        userRepository.save(user);

        ProfileSkillsResource response = controller.getProfileSkills(userId).getSuccessObjectOrThrowException();
        assertEquals(userId, response.getUser());
        assertEquals(1, response.getInnovationAreas().size());
        assertEquals("Innovation area", response.getInnovationAreas().get(0).getName());
        assertEquals(BUSINESS, response.getBusinessType());
        assertEquals("Skills", response.getSkillsAreas());
    }

    @Test
    public void testUpdateProfileSkills() {
        loginCompAdmin();
        UserResource userOne = controller.getUserById(1L).getSuccessObject();
        setLoggedInUser(userOne);

        ProfileSkillsEditResource profileSkillsEditResource = newProfileSkillsEditResource()
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Skills")
                .build();

        RestResult<Void> restResult = controller.updateProfileSkills(1L, profileSkillsEditResource);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void testUpdateProfileAgreement() {
        loginPaulPlum();

        User user = userRepository.findOne(getPaulPlum().getId());
        Long userId = user.getId();

        // Save an agreement as the current agreement
        agreementRepository.deleteAll();
        Agreement agreement = agreementRepository.save(newAgreement()
                .with(id(null))
                .withCurrent(Boolean.TRUE)
                .withText("Agreement text...")
                .build());

        RestResult<Void> restResult = controller.updateProfileAgreement(userId);
        assertTrue(restResult.isSuccess());

        User userAfterUpdate = userRepository.findOne(userId);
        Profile profile = profileRepository.findOne(userAfterUpdate.getProfileId());

        assertEquals(agreement, profile.getAgreement());
    }

    @Ignore("TODO DW - INFUND-936 - this test will cause issues when not running Shib or on an environment like Bamboo where no Shib is available")
    @Test
    public void testCreateLeadApplicant() {
        UserResource userResource = new UserResource();
        userResource.setFirstName("Some");
        userResource.setLastName("How");
        userResource.setPassword("Password123");
        userResource.setEmail("email@Nope.com");
        userResource.setTitle(Title.Miss);
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

    @Test
    public void testGetProfileDetails() {
        loginPaulPlum();

        Long userId = getPaulPlum().getId();
        User user = userRepository.findOne(userId);

        Address address = newAddress()
                .withAddressLine1("10 Test St")
                .withTown("Test Town")
                .build();
        Profile profile = newProfile()
                .withAddress(address)
                .build();
        profile = profileRepository.save(profile);
        user.setProfileId(profile.getId());
        userRepository.save(user);

        UserProfileResource response = controller.getUserProfile(userId).getSuccessObjectOrThrowException();
        assertEquals(address.getAddressLine1(), response.getAddress().getAddressLine1());
        assertEquals(address.getTown(), response.getAddress().getTown());
    }

    @Test
    public void testUpdateProfileDetails() {
        loginPaulPlum();
        User user = userRepository.findOne(getPaulPlum().getId());
        Long userId = user.getId();

        user.setPhoneNumber("12345678");
        user.setDisability(Disability.NO);
        userRepository.save(user);

        UserProfileResource saveResponse = controller.getUserProfile(userId).getSuccessObjectOrThrowException();
        assertEquals("12345678", saveResponse.getPhoneNumber());
        assertEquals(Disability.NO, saveResponse.getDisability());

        UserProfileResource profileDetails = newUserProfileResource()
                .withEthnicity(newEthnicityResource().build())
                .withDisability(Disability.YES)
                .withPhoneNumber("87654321")
                .build();

        RestResult<Void> restResult = controller.updateUserProfile(userId, profileDetails);
        assertTrue(restResult.isSuccess());

        UserProfileResource updateResponse = controller.getUserProfile(userId).getSuccessObjectOrThrowException();
        assertEquals("87654321", updateResponse.getPhoneNumber());
        assertEquals(Disability.YES, updateResponse.getDisability());
    }

    @Test
    public void testGetUserProfileStatus() {
        loginPaulPlum();

        User user = userRepository.findOne(getPaulPlum().getId());
        Long userId = user.getId();
        Profile profile = newProfile()
                .withSkillsAreas("java developer")
                .withAgreementSignedDate(now())
                .build();
        profile = profileRepository.save(profile);

        user.setAffiliations(newAffiliation()
                .withId(null, null)
                .withAffiliationType(EMPLOYER, FAMILY)
                .withUser(user, user)
                .withExists(true, false)
                .build(2));
        user.setProfileId(profile.getId());
        userRepository.save(user);
        flushAndClearSession();

        UserProfileStatusResource profileStatus = controller.getUserProfileStatus(userId).getSuccessObjectOrThrowException();

        UserProfileStatusResource expectedUserProfileStatus = newUserProfileStatusResource()
                .withUser(user.getId())
                .withSkillsComplete(true)
                .withAffliliationsComplete(true)
                .withAgreementComplete(true)
                .build();
        assertEquals(expectedUserProfileStatus, profileStatus);
    }
}
