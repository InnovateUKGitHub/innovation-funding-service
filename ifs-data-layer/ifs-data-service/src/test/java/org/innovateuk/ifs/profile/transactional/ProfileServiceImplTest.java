package org.innovateuk.ifs.profile.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.AgreementMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.AgreementRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZonedDateTime.now;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.AgreementBuilder.newAgreement;
import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

public class ProfileServiceImplTest extends BaseServiceUnitTest<ProfileServiceImpl> {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ProfileRepository profileRepositoryMock;

    @Mock
    private InnovationAreaMapper innovationAreaMapperMock;

    @Mock
    private AgreementRepository agreementRepositoryMock;

    @Mock
    private AgreementMapper agreementMapperMock;

    @Mock
    private AddressMapper addressMapperMock;

    @Mock
    private UserMapper userMapper;

    @Override
    protected ProfileServiceImpl supplyServiceUnderTest() {
        return new ProfileServiceImpl();
    }

    @Test
    public void getProfileSkills() {
        List<InnovationArea> innovationAreas = newInnovationArea()
                .withId(1L, 2L)
                .withName("Data", "Cyber Security")
                .build(2);

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withId(1L, 2L)
                .withName("Data", "Cyber Security")
                .build(2);

        User existingUser = newUser().build();
        Profile profile = newProfile()
                .withAddress(newAddress().build())
                .withAgreement(newAgreement().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .withInnovationAreas(innovationAreas)
                .build();
        existingUser.setProfileId(profile.getId());

        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(profileRepositoryMock.findById(existingUser.getProfileId())).thenReturn(Optional.of(profile));
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(0))).thenReturn(innovationAreaResources.get(0));
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(1))).thenReturn(innovationAreaResources.get(1));

        ProfileSkillsResource response = service.getProfileSkills(existingUser.getId()).getSuccess();
        assertEquals(existingUser.getId(), response.getUser());
        assertThat(response.getInnovationAreas(), containsInAnyOrder(innovationAreaResources.toArray(new
                InnovationAreaResource[innovationAreaResources.size()])));
        assertEquals(ACADEMIC, response.getBusinessType());
        assertEquals("Skills", response.getSkillsAreas());

        InOrder inOrder = inOrder(userRepositoryMock, profileRepositoryMock, innovationAreaMapperMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(profileRepositoryMock).findById(existingUser.getProfileId());
        inOrder.verify(innovationAreaMapperMock, times(2)).mapToResource(isA(InnovationArea.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileSkills_userDoesNotExist() {
        long userIdNotExists = 1L;

        ServiceResult<ProfileSkillsResource> response = service.getProfileSkills(userIdNotExists);
        assertTrue(response.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findById(userIdNotExists);
    }

    @Test
    public void getProfileSkills_noSkills() {
        User existingUser = newUser()
                .withProfileId(1L)
                .build();

        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        ProfileSkillsResource expected = newProfileSkillsResource()
                .withUser(existingUser.getId())
                .build();

        ProfileSkillsResource response = service.getProfileSkills(existingUser.getId()).getSuccess();
        assertEquals(expected, response);

        verify(userRepositoryMock).findById(existingUser.getId());
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void getProfileSkills_userDoesNotHaveProfileYet() {
        User existingUser = newUser()
                .build();

        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        ProfileSkillsResource expected = newProfileSkillsResource()
                .withUser(existingUser.getId())
                .build();

        ProfileSkillsResource response = service.getProfileSkills(existingUser.getId()).getSuccess();
        assertEquals(expected, response);

        verify(userRepositoryMock).findById(existingUser.getId());
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void updateProfileSkills() {
        long userId = 1L;

        Profile existingProfile = newProfile()
                .withAddress(newAddress().build())
                .withAgreement(newAgreement().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .build();
        User existingUser = newUser()
                .withId(userId)
                .withProfileId(existingProfile.getId())
                .build();

        when(profileRepositoryMock.findById(existingProfile.getId())).thenReturn(Optional.of(existingProfile));
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        Profile updatedProfile = newProfile()
                .with(id(existingUser.getProfileId()))
                .withAddress(existingProfile.getAddress())
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Updated")
                .withAgreement(existingProfile.getAgreement())
                .build();
        when(profileRepositoryMock.save(updatedProfile)).thenReturn(updatedProfile);

        ServiceResult<Void> result = service.updateProfileSkills(existingUser.getId(), newProfileSkillsEditResource()
                .withUser(existingUser.getId())
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Updated")
                .build());

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(userId);
        inOrder.verify(profileRepositoryMock).save(updatedProfile);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileSkills_userDoesNotExist() {
        long userId = 1L;

        ServiceResult<Void> result = service.updateProfileSkills(userId, newProfileSkillsEditResource().build());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, userId)));

        verify(userRepositoryMock).findById(userId);
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void updateProfileSkills_userDoesNotHaveProfileYet() {
        long userId = 1L;

        Profile profile = newProfile()
                .withId(23L)
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Updated")
                .build();
        User existingUser = newUser()
                .withId(userId)
                .withProfileId(profile.getId())
                .build();

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(existingUser));
        when(profileRepositoryMock.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(newProfile().build(), profile);

        ServiceResult<Void> result = service.updateProfileSkills(existingUser.getId(), newProfileSkillsEditResource()
                .withUser(existingUser.getId())
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Updated")
                .build());

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(userId);
        inOrder.verify(profileRepositoryMock).findById(profile.getId());
        inOrder.verify(profileRepositoryMock).save(profile);
        inOrder.verifyNoMoreInteractions();
    }


    @Test
    public void getProfileAgreement() {
        Agreement currentAgreement = newAgreement()
                .build();

        AgreementResource currentAgreementResource = newAgreementResource().build();

        ZonedDateTime agreementSignedDate = now();

        Profile profile = newProfile()
                .withAgreement(currentAgreement)
                .withAgreementSignedDate(agreementSignedDate)
                .build();
        User existingUser = newUser()
                .withProfileId(profile.getId())
                .build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(agreementMapperMock.mapToResource(currentAgreement)).thenReturn(currentAgreementResource);
        when(profileRepositoryMock.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        ProfileAgreementResource expected = newProfileAgreementResource()
                .withUser(existingUser.getId())
                .withAgreement(currentAgreementResource)
                .withCurrentAgreement(true)
                .withAgreementSignedDate(agreementSignedDate)
                .build();

        ProfileAgreementResource response = service.getProfileAgreement(existingUser.getId()).getSuccess();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(agreementRepositoryMock, userRepositoryMock, agreementMapperMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(agreementMapperMock).mapToResource(currentAgreement);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileAgreement_userDoesNotExist() {
        long userIdNotExists = 1L;

        ServiceResult<ProfileAgreementResource> response = service.getProfileAgreement(userIdNotExists);
        assertTrue(response.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findById(userIdNotExists);
        verifyZeroInteractions(agreementRepositoryMock);
        verifyZeroInteractions(agreementMapperMock);
    }

    @Test
    public void getProfileAgreement_noAgreement() {
        Agreement currentAgreement = newAgreement()
                .build();

        AgreementResource currentAgreementResource = newAgreementResource().build();

        // Profile has no agreement or signed date
        User existingUser = newUser()
                .withProfileId(1L)
                .build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(agreementMapperMock.mapToResource(currentAgreement)).thenReturn(currentAgreementResource);
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        ProfileAgreementResource expected = newProfileAgreementResource()
                .withUser(existingUser.getId())
                .withAgreement(currentAgreementResource)
                .withCurrentAgreement(false)
                .withAgreementSignedDate((ZonedDateTime) null)
                .build();

        ProfileAgreementResource response = service.getProfileAgreement(existingUser.getId()).getSuccess();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(agreementRepositoryMock, userRepositoryMock, agreementMapperMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(agreementMapperMock).mapToResource(currentAgreement);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileAgreement_noCurrentAgreement() {
        Agreement currentAgreement = newAgreement()
                .build();

        AgreementResource currentAgreementResource = newAgreementResource().build();

        // Profile has an agreement and a signed date, but not the current one
        User existingUser = newUser()
                .withProfileId(10L)
                .build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(agreementMapperMock.mapToResource(currentAgreement)).thenReturn(currentAgreementResource);
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        ProfileAgreementResource expected = newProfileAgreementResource()
                .withUser(existingUser.getId())
                .withAgreement(currentAgreementResource)
                .withCurrentAgreement(false)
                .withAgreementSignedDate((ZonedDateTime) null)
                .build();

        ProfileAgreementResource response = service.getProfileAgreement(existingUser.getId()).getSuccess();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(agreementRepositoryMock, userRepositoryMock, agreementMapperMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(agreementMapperMock).mapToResource(currentAgreement);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileAgreement_userDoesNotHaveProfileYet() {
        Agreement currentAgreement = newAgreement()
                .build();

        AgreementResource currentAgreementResource = newAgreementResource().build();

        User existingUser = newUser()
                .build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(agreementMapperMock.mapToResource(currentAgreement)).thenReturn(currentAgreementResource);
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        ProfileAgreementResource expected = newProfileAgreementResource()
                .withUser(existingUser.getId())
                .withAgreement(currentAgreementResource)
                .withCurrentAgreement(false)
                .withAgreementSignedDate((ZonedDateTime) null)
                .build();

        ProfileAgreementResource response = service.getProfileAgreement(existingUser.getId()).getSuccess();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(agreementRepositoryMock, userRepositoryMock, agreementMapperMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(agreementMapperMock).mapToResource(currentAgreement);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement() {
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.of(2016, 10, 11, 12, 13, 14, 0, ZoneId.systemDefault());

        Profile profile = newProfile()
                .withAgreement(newAgreement().build())
                .withAgreementSignedDate((ZonedDateTime) null)
                .build();
        User existingUser = newUser()
                .withProfileId(profile.getId())
                .build();
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(profileRepositoryMock.findById(profile.getId())).thenReturn(Optional.of(profile));

        Agreement currentAgreement = newAgreement()
                .build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);

        when(userRepositoryMock.save(createUserExpectations(existingUser.getId(), newProfile()
                .withId(existingUser.getProfileId())
                .withAgreement(currentAgreement)
                .withAgreementSignedDate(expectedAgreementSignedDate)
                .build()))).thenReturn(newUser().build());

        ServiceResult<Void> result = service.updateProfileAgreement(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, agreementRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findById(profile.getId());
        inOrder.verify(profileRepositoryMock).save(profile);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement_userDoesNotExist() {
        long userIdNotExists = 1L;

        ServiceResult<Void> result = service.updateProfileAgreement(userIdNotExists);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findById(userIdNotExists);
        verifyZeroInteractions(agreementRepositoryMock);
        verifyZeroInteractions(agreementMapperMock);
    }

    @Test
    public void updateProfileAgreement_noAgreement() {
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.of(2016, 10, 11, 12, 13, 14, 0, ZoneId.systemDefault());

        // Profile has no agreement or signed date
        Profile initialProfile = newProfile()
                .build();
        User existingUser = newUser()
                .withProfileId(initialProfile.getId())
                .build();
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        Agreement currentAgreement = newAgreement()
                .build();
        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);

        Profile updatedProfile = newProfile()
                .with(id(existingUser.getProfileId()))
                .withAgreement(currentAgreement)
                .withAgreementSignedDate(expectedAgreementSignedDate)
                .build();
        when(profileRepositoryMock.save(updatedProfile)).thenReturn(updatedProfile);
        when(profileRepositoryMock.findById(initialProfile.getId()))
                .thenReturn(Optional.of(initialProfile), Optional.of(initialProfile), Optional.of(updatedProfile));

        ServiceResult<Void> result = service.updateProfileAgreement(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, agreementRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findById(updatedProfile.getId());
        inOrder.verify(profileRepositoryMock).save(any(Profile.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement_noCurrentAgreement() {
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.of(2016, 10, 11, 12, 13, 14, 0, ZoneId.systemDefault());

        // Profile has a agreement and a signed date, but not the current one
        Profile initialProfile = newProfile()
                .withAgreement(newAgreement().withId(1L).build())
                .withAgreementSignedDate(now())
                .build();
        User existingUser = newUser()
                .withProfileId(initialProfile.getId())
                .build();
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        Agreement currentAgreement = newAgreement()
                .withId(2L)
                .build();
        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);

        Profile updatedProfile = newProfile()
                .with(id(existingUser.getProfileId()))
                .withAgreement(currentAgreement)
                .withAgreementSignedDate(expectedAgreementSignedDate)
                .build();
        when(profileRepositoryMock.save(updatedProfile)).thenReturn(updatedProfile);
        when(profileRepositoryMock.findById(initialProfile.getId()))
                .thenReturn(Optional.of(initialProfile), Optional.of(initialProfile), Optional.of(updatedProfile));

        ServiceResult<Void> result = service.updateProfileAgreement(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, agreementRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findById(updatedProfile.getId());
        inOrder.verify(profileRepositoryMock).save(any(Profile.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement_agreementAlreadySigned() {
        Agreement currentAgreement = newAgreement()
                .build();
        Profile profile = newProfile()
                .withAgreement(currentAgreement)
                .withAgreementSignedDate(now())
                .build();
        User existingUser = newUser()
                .withProfileId(profile.getId())
                .build();
        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(profileRepositoryMock.findById(existingUser.getProfileId())).thenReturn(Optional.of(profile));
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        ServiceResult<Void> result = service.updateProfileAgreement(existingUser.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(badRequestError("validation.assessorprofileagreementform.terms.alreadysigned")));

        InOrder inOrder = inOrder(userRepositoryMock, agreementRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement_userDoesNotHaveProfileYet() {
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.of(2016, 10, 11, 12, 13, 14, 0, ZoneId.systemDefault());

        User existingUser = newUser()
                .withId(1L)
                .build();
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        Agreement currentAgreement = newAgreement()
                .build();
        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);

        Profile newProfile = newProfile()
                .with(id(null))
                .build();
        Long profileId = 23L;
        Profile savedProfile = newProfile()
                .withId(profileId)
                .build();
        Profile profileWithAgreement = newProfile()
                .withId(profileId)
                .withAgreement(currentAgreement)
                .withAgreementSignedDate(expectedAgreementSignedDate)
                .build();
        when(profileRepositoryMock.save(newProfile)).thenReturn(savedProfile);
        when(profileRepositoryMock.findById(profileId)).thenReturn(Optional.of(savedProfile), Optional.of(profileWithAgreement));

        ServiceResult<Void> result = service.updateProfileAgreement(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, agreementRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock).findById(profileId);
        inOrder.verify(profileRepositoryMock).save(any(Profile.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUserProfileStatus() {
        User user = newUser().build();
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(newUserProfileStatusResource().withUser(user.getId()).build(), result.getSuccess());

        verify(userRepositoryMock, only()).findById(user.getId());
    }

    @Test
    public void getUserProfileStatus_complete() {
        Profile profile = newProfile()
                .withSkillsAreas("skills")
                .withAgreementSignedDate(now())
                .build();
        User user = newUser()
                .withAffiliations( newAffiliation().withModifiedOn(now().minusYears(0)).build(1) )
                .withProfileId(profile.getId())
                .build();

        when(profileRepositoryMock.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(
                newUserProfileStatusResource()
                        .withUser(user.getId())
                        .withSkillsComplete(true)
                        .withAffliliationsComplete(true)
                        .withAgreementComplete(true)
                        .build(),
                result.getSuccess()
        );

        verify(userRepositoryMock, only()).findById(user.getId());
    }

    @Test
    public void getUserProfileStatus_skillsComplete() {
        Profile profile = newProfile()
                .withSkillsAreas("skills")
                .build();
        User user = newUser()
                .withProfileId(profile.getId())
                .build();

        when(profileRepositoryMock.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(
                newUserProfileStatusResource()
                        .withUser(user.getId())
                        .withSkillsComplete(true)
                        .withAffliliationsComplete(false)
                        .withAgreementComplete(false)
                        .build(),
                result.getSuccess()
        );

        verify(userRepositoryMock, only()).findById(user.getId());
    }

    @Test
    public void getUserProfileStatus_affiliationsComplete() {
        User user = newUser()
                .withAffiliations( newAffiliation().withModifiedOn(now().minusYears(0)).build(1) )
                .build();

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(
                newUserProfileStatusResource()
                        .withUser(user.getId())
                        .withSkillsComplete(false)
                        .withAffliliationsComplete(true)
                        .withAgreementComplete(false)
                        .build(),
                result.getSuccess()
        );

        verify(userRepositoryMock, only()).findById(user.getId());
    }

    // TODO test affiliations expired

    @Test
    public void getUserProfileStatus_agreementComplete() {
        Profile profile = newProfile()
                .withAgreementSignedDate(now())
                .build();
        User user = newUser()
                .withProfileId(profile.getId())
                .build();

        when(profileRepositoryMock.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(
                newUserProfileStatusResource()
                        .withUser(user.getId())
                        .withSkillsComplete(false)
                        .withAffliliationsComplete(false)
                        .withAgreementComplete(true)
                        .build(),
                result.getSuccess()
        );

        verify(userRepositoryMock, only()).findById(user.getId());
    }

    @Test
    public void getUserProfile() {
        User createdByUser = newUser().withFirstName("abc").withLastName("def").build();

        Profile existingProfile = newProfile()
                .withAddress(newAddress().withId(1L).build())
                .withAgreement(newAgreement().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .withCreatedBy(createdByUser)
                .withCreatedOn(now())
                .withModifiedBy(createdByUser)
                .withModifiedOn(now())
                .build();
        User existingUser = newUser()
                .withProfileId(existingProfile.getId())
                .build();

        AddressResource addressResource = newAddressResource().withId(1L).build();

        when(profileRepositoryMock.findById(existingProfile.getId())).thenReturn(Optional.of(existingProfile));
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(addressMapperMock.mapToResource(existingProfile.getAddress())).thenReturn(addressResource);

        UserProfileResource expected = newUserProfileResource()
                .withUser(existingUser.getId())
                .withFirstName(existingUser.getFirstName())
                .withLastName(existingUser.getLastName())
                .withEmail(existingUser.getEmail())
                .withAddress(addressResource)
                .withCreatedBy(createdByUser.getName())
                .withCreatedOn(now())
                .withModifiedBy(createdByUser.getName())
                .withModifiedOn(now())
                .build();

        UserProfileResource response = service.getUserProfile(existingUser.getId()).getSuccess();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(userRepositoryMock, addressMapperMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(addressMapperMock).mapToResource(existingProfile.getAddress());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUserProfile_userDoesNotHaveProfileYet() {
        User existingUser = newUser()
                .build();

        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        UserProfileResource expected = newUserProfileResource()
                .withUser(existingUser.getId())
                .withFirstName(existingUser.getFirstName())
                .withLastName(existingUser.getLastName())
                .withEmail(existingUser.getEmail())
                .build();

        UserProfileResource response = service.getUserProfile(existingUser.getId()).getSuccess();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(userRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(addressMapperMock);
    }

    @Test
    public void updateUserProfile() {
        Long userId = 1L;

        Profile originalProfile = newProfile()
                .withAddress(newAddress().build())
                .build();
        User existingUser = newUser()
                .withProfileId(originalProfile.getId())
                .build();

        when(profileRepositoryMock.findById(originalProfile.getId())).thenReturn(Optional.of(originalProfile));
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(existingUser));

        AddressResource addressResource = newAddressResource().build();
        Address address = newAddress().build();
        when(addressMapperMock.mapToDomain(addressResource)).thenReturn(address);

        Profile updatedProfile = newProfile()
                .with(id(existingUser.getProfileId()))
                .withAddress(address)
                .build();
        when(profileRepositoryMock.save(updatedProfile)).thenReturn(updatedProfile);

        ServiceResult<UserResource> result = service.updateUserProfile(userId, newUserProfileResource()
                .withAddress(addressResource)
                .build());

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, addressMapperMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(userId);
        inOrder.verify(profileRepositoryMock).findById(originalProfile.getId());
        inOrder.verify(addressMapperMock).mapToDomain(addressResource);
        inOrder.verify(profileRepositoryMock).save(updatedProfile);

        inOrder.verifyNoMoreInteractions();
    }

    private User createUserExpectations(Long userId, Profile profile) {
        return createLambdaMatcher(user -> {
            assertEquals(userId, user.getId());
            assertEquals(profile, user.getProfileId());
        });
    }

    private User createUserExpectations(Long userId, List<Affiliation> affiliations) {
        return createLambdaMatcher(user -> {
            assertEquals(userId, user.getId());
            assertEquals(affiliations, user.getAffiliations());
        });
    }
}