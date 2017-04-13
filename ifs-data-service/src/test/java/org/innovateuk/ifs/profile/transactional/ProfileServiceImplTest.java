package org.innovateuk.ifs.profile.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.user.domain.*;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.mockito.InOrder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.AgreementBuilder.newAgreement;
import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.innovateuk.ifs.user.builder.EthnicityBuilder.newEthnicity;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class ProfileServiceImplTest extends BaseServiceUnitTest<ProfileServiceImpl> {

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

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);
        when(profileRepositoryMock.findOne(existingUser.getProfileId())).thenReturn(profile);
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(0))).thenReturn(innovationAreaResources.get(0));
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(1))).thenReturn(innovationAreaResources.get(1));

        ProfileSkillsResource response = service.getProfileSkills(existingUser.getId()).getSuccessObject();
        assertEquals(existingUser.getId(), response.getUser());
        assertThat(response.getInnovationAreas(), containsInAnyOrder(innovationAreaResources.toArray(new
                InnovationAreaResource[innovationAreaResources.size()])));
        assertEquals(ACADEMIC, response.getBusinessType());
        assertEquals("Skills", response.getSkillsAreas());

        InOrder inOrder = inOrder(userRepositoryMock, profileRepositoryMock, innovationAreaMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(profileRepositoryMock).findOne(existingUser.getProfileId());
        inOrder.verify(innovationAreaMapperMock, times(2)).mapToResource(isA(InnovationArea.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileSkills_userDoesNotExist() throws Exception {
        long userIdNotExists = 1L;

        ServiceResult<ProfileSkillsResource> response = service.getProfileSkills(userIdNotExists);
        assertTrue(response.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findOne(userIdNotExists);
    }

    @Test
    public void getProfileSkills_noSkills() {
        User existingUser = newUser()
                .withProfileId(1L)
                .build();

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileSkillsResource expected = newProfileSkillsResource()
                .withUser(existingUser.getId())
                .build();

        ProfileSkillsResource response = service.getProfileSkills(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        verify(userRepositoryMock).findOne(existingUser.getId());
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void getProfileSkills_userDoesNotHaveProfileYet() throws Exception {
        User existingUser = newUser()
                .build();

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileSkillsResource expected = newProfileSkillsResource()
                .withUser(existingUser.getId())
                .build();

        ProfileSkillsResource response = service.getProfileSkills(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        verify(userRepositoryMock).findOne(existingUser.getId());
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

        when(profileRepositoryMock.findOne(existingProfile.getId())).thenReturn(existingProfile);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

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
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(profileRepositoryMock).save(updatedProfile);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileSkills_userDoesNotExist() {
        long userId = 1L;

        ServiceResult<Void> result = service.updateProfileSkills(userId, newProfileSkillsEditResource().build());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, userId)));

        verify(userRepositoryMock).findOne(userId);
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void updateProfileSkills_userDoesNotHaveProfileYet() throws Exception {
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

        when(userRepositoryMock.findOne(userId)).thenReturn(existingUser);
        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(newProfile().build(), profile);

        ServiceResult<Void> result = service.updateProfileSkills(existingUser.getId(), newProfileSkillsEditResource()
                .withUser(existingUser.getId())
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Updated")
                .build());

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(profileRepositoryMock).findOne(profile.getId());
        inOrder.verify(profileRepositoryMock).save(profile);
        inOrder.verifyNoMoreInteractions();
    }


    @Test
    public void getProfileAgreement() throws Exception {
        Agreement currentAgreement = newAgreement()
                .build();

        AgreementResource currentAgreementResource = newAgreementResource().build();

        ZonedDateTime agreementSignedDate = ZonedDateTime.now();

        Profile profile = newProfile()
                .withAgreement(currentAgreement)
                .withAgreementSignedDate(agreementSignedDate)
                .build();
        User existingUser = newUser()
                .withProfileId(profile.getId())
                .build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(agreementMapperMock.mapToResource(currentAgreement)).thenReturn(currentAgreementResource);
        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileAgreementResource expected = newProfileAgreementResource()
                .withUser(existingUser.getId())
                .withAgreement(currentAgreementResource)
                .withCurrentAgreement(true)
                .withAgreementSignedDate(agreementSignedDate)
                .build();

        ProfileAgreementResource response = service.getProfileAgreement(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(agreementRepositoryMock, userRepositoryMock, agreementMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(agreementMapperMock).mapToResource(currentAgreement);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileAgreement_userDoesNotExist() throws Exception {
        long userIdNotExists = 1L;

        ServiceResult<ProfileAgreementResource> response = service.getProfileAgreement(userIdNotExists);
        assertTrue(response.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findOne(userIdNotExists);
        verifyZeroInteractions(agreementRepositoryMock);
        verifyZeroInteractions(agreementMapperMock);
    }

    @Test
    public void getProfileAgreement_noAgreement() throws Exception {
        Agreement currentAgreement = newAgreement()
                .build();

        AgreementResource currentAgreementResource = newAgreementResource().build();

        // Profile has no agreement or signed date
        User existingUser = newUser()
                .withProfileId(1L)
                .build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(agreementMapperMock.mapToResource(currentAgreement)).thenReturn(currentAgreementResource);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileAgreementResource expected = newProfileAgreementResource()
                .withUser(existingUser.getId())
                .withAgreement(currentAgreementResource)
                .withCurrentAgreement(false)
                .withAgreementSignedDate((ZonedDateTime) null)
                .build();

        ProfileAgreementResource response = service.getProfileAgreement(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(agreementRepositoryMock, userRepositoryMock, agreementMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(agreementMapperMock).mapToResource(currentAgreement);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileAgreement_noCurrentAgreement() throws Exception {
        Agreement currentAgreement = newAgreement()
                .build();

        AgreementResource currentAgreementResource = newAgreementResource().build();

        // Profile has an agreement and a signed date, but not the current one
        User existingUser = newUser()
                .withProfileId(10L)
                .build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(agreementMapperMock.mapToResource(currentAgreement)).thenReturn(currentAgreementResource);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileAgreementResource expected = newProfileAgreementResource()
                .withUser(existingUser.getId())
                .withAgreement(currentAgreementResource)
                .withCurrentAgreement(false)
                .withAgreementSignedDate((ZonedDateTime) null)
                .build();

        ProfileAgreementResource response = service.getProfileAgreement(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(agreementRepositoryMock, userRepositoryMock, agreementMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(agreementMapperMock).mapToResource(currentAgreement);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileAgreement_userDoesNotHaveProfileYet() throws Exception {
        Agreement currentAgreement = newAgreement()
                .build();

        AgreementResource currentAgreementResource = newAgreementResource().build();

        User existingUser = newUser()
                .build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(agreementMapperMock.mapToResource(currentAgreement)).thenReturn(currentAgreementResource);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileAgreementResource expected = newProfileAgreementResource()
                .withUser(existingUser.getId())
                .withAgreement(currentAgreementResource)
                .withCurrentAgreement(false)
                .withAgreementSignedDate((ZonedDateTime) null)
                .build();

        ProfileAgreementResource response = service.getProfileAgreement(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(agreementRepositoryMock, userRepositoryMock, agreementMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(agreementMapperMock).mapToResource(currentAgreement);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement() throws Exception {
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.of(2016, 10, 11, 12, 13, 14, 0, ZoneId.systemDefault());

        Profile profile = newProfile()
                .withAgreement(newAgreement().build())
                .withAgreementSignedDate((ZonedDateTime) null)
                .build();
        User existingUser = newUser()
                .withProfileId(profile.getId())
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);
        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);

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
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findOne(profile.getId());
        inOrder.verify(profileRepositoryMock).save(profile);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement_userDoesNotExist() throws Exception {
        long userIdNotExists = 1L;

        ServiceResult<Void> result = service.updateProfileAgreement(userIdNotExists);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findOne(userIdNotExists);
        verifyZeroInteractions(agreementRepositoryMock);
        verifyZeroInteractions(agreementMapperMock);
    }

    @Test
    public void updateProfileAgreement_noAgreement() throws Exception {
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.of(2016, 10, 11, 12, 13, 14, 0, ZoneId.systemDefault());

        // Profile has no agreement or signed date
        Profile initialProfile = newProfile()
                .build();
        User existingUser = newUser()
                .withProfileId(initialProfile.getId())
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        Agreement currentAgreement = newAgreement()
                .build();
        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);

        Profile updatedProfile = newProfile()
                .with(id(existingUser.getProfileId()))
                .withAgreement(currentAgreement)
                .withAgreementSignedDate(expectedAgreementSignedDate)
                .build();
        when(profileRepositoryMock.save(updatedProfile)).thenReturn(updatedProfile);
        when(profileRepositoryMock.findOne(initialProfile.getId()))
                .thenReturn(initialProfile, initialProfile, updatedProfile);

        ServiceResult<Void> result = service.updateProfileAgreement(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, agreementRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findOne(updatedProfile.getId());
        inOrder.verify(profileRepositoryMock).save(any(Profile.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement_noCurrentAgreement() throws Exception {
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.of(2016, 10, 11, 12, 13, 14, 0, ZoneId.systemDefault());

        // Profile has a agreement and a signed date, but not the current one
        Profile initialProfile = newProfile()
                .withAgreement(newAgreement().withId(1L).build())
                .withAgreementSignedDate(ZonedDateTime.now())
                .build();
        User existingUser = newUser()
                .withProfileId(initialProfile.getId())
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

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
        when(profileRepositoryMock.findOne(initialProfile.getId()))
                .thenReturn(initialProfile, initialProfile, updatedProfile);

        ServiceResult<Void> result = service.updateProfileAgreement(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, agreementRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findOne(updatedProfile.getId());
        inOrder.verify(profileRepositoryMock).save(any(Profile.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement_agreementAlreadySigned() throws Exception {
        Agreement currentAgreement = newAgreement()
                .build();
        Profile profile = newProfile()
                .withAgreement(currentAgreement)
                .withAgreementSignedDate(ZonedDateTime.now())
                .build();
        User existingUser = newUser()
                .withProfileId(profile.getId())
                .build();
        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(currentAgreement);
        when(profileRepositoryMock.findOne(existingUser.getProfileId())).thenReturn(profile);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ServiceResult<Void> result = service.updateProfileAgreement(existingUser.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(badRequestError("validation.assessorprofileagreementform.terms.alreadysigned")));

        InOrder inOrder = inOrder(userRepositoryMock, agreementRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileAgreement_userDoesNotHaveProfileYet() throws Exception {
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.of(2016, 10, 11, 12, 13, 14, 0, ZoneId.systemDefault());

        User existingUser = newUser()
                .withId(1L)
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

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
        when(profileRepositoryMock.findOne(profileId)).thenReturn(savedProfile, profileWithAgreement);

        ServiceResult<Void> result = service.updateProfileAgreement(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, agreementRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(agreementRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock).findOne(profileId);
        inOrder.verify(profileRepositoryMock).save(any(Profile.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUserProfileStatus() throws Exception {
        User user = newUser().build();
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(newUserProfileStatusResource().withUser(user.getId()).build(), result.getSuccessObject());

        verify(userRepositoryMock, only()).findOne(user.getId());
    }

    @Test
    public void getUserProfileStatus_complete() throws Exception {
        Profile profile = newProfile()
                .withSkillsAreas("skills")
                .withAgreementSignedDate(ZonedDateTime.now())
                .build();
        User user = newUser()
                .withAffiliations(asList(newAffiliation().build()))
                .withProfileId(profile.getId())
                .build();

        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(
                newUserProfileStatusResource()
                        .withUser(user.getId())
                        .withSkillsComplete(true)
                        .withAffliliationsComplete(true)
                        .withAgreementComplete(true)
                        .build(),
                result.getSuccessObject()
        );

        verify(userRepositoryMock, only()).findOne(user.getId());
    }

    @Test
    public void getUserProfileStatus_skillsComplete() throws Exception {
        Profile profile = newProfile()
                .withSkillsAreas("skills")
                .build();
        User user = newUser()
                .withProfileId(profile.getId())
                .build();

        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(
                newUserProfileStatusResource()
                        .withUser(user.getId())
                        .withSkillsComplete(true)
                        .withAffliliationsComplete(false)
                        .withAgreementComplete(false)
                        .build(),
                result.getSuccessObject()
        );

        verify(userRepositoryMock, only()).findOne(user.getId());
    }

    @Test
    public void getUserProfileStatus_affiliationsComplete() throws Exception {
        User user = newUser()
                .withAffiliations(asList(newAffiliation().build()))
                .build();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(
                newUserProfileStatusResource()
                        .withUser(user.getId())
                        .withSkillsComplete(false)
                        .withAffliliationsComplete(true)
                        .withAgreementComplete(false)
                        .build(),
                result.getSuccessObject()
        );

        verify(userRepositoryMock, only()).findOne(user.getId());
    }

    @Test
    public void getUserProfileStatus_agreementComplete() throws Exception {
        Profile profile = newProfile()
                .withAgreementSignedDate(ZonedDateTime.now())
                .build();
        User user = newUser()
                .withProfileId(profile.getId())
                .build();

        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(
                newUserProfileStatusResource()
                        .withUser(user.getId())
                        .withSkillsComplete(false)
                        .withAffliliationsComplete(false)
                        .withAgreementComplete(true)
                        .build(),
                result.getSuccessObject()
        );

        verify(userRepositoryMock, only()).findOne(user.getId());
    }

    @Test
    public void getUserProfile() {
        Profile existingProfile = newProfile()
                .withAddress(newAddress().withId(1L).build())
                .withAgreement(newAgreement().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .build();
        User existingUser = newUser()
                .withProfileId(existingProfile.getId())
                .withEthnicity(newEthnicity().build())
                .build();

        AddressResource addressResource = newAddressResource().withId(1L).build();
        EthnicityResource ethnicityResource = newEthnicityResource().build();

        when(profileRepositoryMock.findOne(existingProfile.getId())).thenReturn(existingProfile);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);
        when(addressMapperMock.mapToResource(existingProfile.getAddress())).thenReturn(addressResource);
        when(ethnicityMapperMock.mapToResource(existingUser.getEthnicity())).thenReturn(ethnicityResource);

        UserProfileResource expected = newUserProfileResource()
                .withUser(existingUser.getId())
                .withFirstName(existingUser.getFirstName())
                .withLastName(existingUser.getLastName())
                .withEmail(existingUser.getEmail())
                .withAddress(addressResource)
                .withEthnicity(ethnicityResource)
                .build();

        UserProfileResource response = service.getUserProfile(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(userRepositoryMock, ethnicityMapperMock, addressMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(ethnicityMapperMock).mapToResource(existingUser.getEthnicity());
        inOrder.verify(addressMapperMock).mapToResource(existingProfile.getAddress());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUserProfile_userDoesNotHaveProfileYet() {
        User existingUser = newUser()
                .withEthnicity(newEthnicity().build())
                .build();

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        UserProfileResource expected = newUserProfileResource()
                .withUser(existingUser.getId())
                .withFirstName(existingUser.getFirstName())
                .withLastName(existingUser.getLastName())
                .withEmail(existingUser.getEmail())
                .build();

        UserProfileResource response = service.getUserProfile(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(userRepositoryMock, ethnicityMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(ethnicityMapperMock).mapToResource(existingUser.getEthnicity());
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
                .withEthnicity(newEthnicity().build())
                .build();

        when(profileRepositoryMock.findOne(originalProfile.getId())).thenReturn(originalProfile);
        when(userRepositoryMock.findOne(userId)).thenReturn(existingUser);

        EthnicityResource ethnicityResource = newEthnicityResource().build();
        Ethnicity ethnicity = newEthnicity().build();
        when(ethnicityMapperMock.mapIdToDomain(ethnicityResource.getId())).thenReturn(ethnicity);

        AddressResource addressResource = newAddressResource().build();
        Address address = newAddress().build();
        when(addressMapperMock.mapToDomain(addressResource)).thenReturn(address);

        Profile updatedProfile = newProfile()
                .with(id(existingUser.getProfileId()))
                .withAddress(address)
                .build();
        when(profileRepositoryMock.save(updatedProfile)).thenReturn(updatedProfile);

        ServiceResult<Void> result = service.updateUserProfile(userId, newUserProfileResource()
                .withEthnicity(ethnicityResource)
                .withAddress(addressResource)
                .build());

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, ethnicityMapperMock, addressMapperMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(ethnicityMapperMock).mapIdToDomain(ethnicityResource.getId());
        inOrder.verify(profileRepositoryMock).findOne(originalProfile.getId());
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

    private User createUserExpectations(Long userId, Ethnicity ethnicity, Profile profile) {
        return createLambdaMatcher(user -> {
            assertEquals(userId, user.getId());
            assertEquals(ethnicity, user.getEthnicity());
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
