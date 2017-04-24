package org.innovateuk.ifs.profile.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.AgreementRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.AgreementBuilder.newAgreement;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.EMPLOYER;
import static org.innovateuk.ifs.user.resource.AffiliationType.FAMILY;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProfileControllerIntegrationTest extends BaseControllerIntegrationTest<ProfileController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(ProfileController controller) {
        this.controller = controller;
    }

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private AgreementRepository agreementRepository;

    @Autowired
    private UserMapper userMapper;

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
        UserResource userOne = userMapper.mapToResource(userRepository.findOne(1L));
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
