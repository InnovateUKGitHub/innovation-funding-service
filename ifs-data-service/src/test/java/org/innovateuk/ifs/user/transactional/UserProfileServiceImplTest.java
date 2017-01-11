package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.*;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.domain.Contract;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.ContractBuilder.newContract;
import static org.innovateuk.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static org.innovateuk.ifs.user.builder.EthnicityBuilder.newEthnicity;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.ProfileContractResourceBuilder.newProfileContractResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Tests around the User Profile Service
 */
public class UserProfileServiceImplTest extends BaseServiceUnitTest<UserProfileServiceImpl> {
    @Override
    protected UserProfileServiceImpl supplyServiceUnderTest() {
        return new UserProfileServiceImpl();
    }

    @Test
    public void getProfileSkills() {
        User existingUser = newUser().build();
        Profile profile = newProfile()
                .withAddress(newAddress().build())
                .withContract(newContract().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .build();
        existingUser.setProfileId(profile.getId());

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);
        when(profileRepositoryMock.findOne(existingUser.getProfileId())).thenReturn(profile);

        ProfileSkillsResource expected = newProfileSkillsResource()
                .withUser(existingUser.getId())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .build();

        ProfileSkillsResource response = service.getProfileSkills(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        verify(userRepositoryMock).findOne(existingUser.getId());
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void getProfileSkills_userDoesNotExist() throws Exception {
        Long userIdNotExists = 1L;

        ServiceResult<ProfileSkillsResource> response = service.getProfileSkills(userIdNotExists);
        assertTrue(response.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findOne(userIdNotExists);
    }

    @Test
    public void getProfileSkills_noSkills() {
        User existingUser = newUser()
                .withProfile(newProfile()
                        .withAddress(newAddress().build())
                        .withContract(newContract().build())
                        .build())
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
        Long userId = 1L;

        Profile existingProfile = newProfile()
                .withAddress(newAddress().build())
                .withContract(newContract().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .build();
        User existingUser = newUser()
                .withId(userId)
                .withProfile(existingProfile)
                .build();

        when(profileRepositoryMock.findOne(existingProfile.getId())).thenReturn(existingProfile);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        Profile updatedProfile = newProfile()
                .with(id(existingUser.getProfileId()))
                .withAddress(existingProfile.getAddress())
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Updated")
                .withContract(existingProfile.getContract())
                .build();
        when(profileRepositoryMock.save(updatedProfile)).thenReturn(updatedProfile);

        ServiceResult<Void> result = service.updateProfileSkills(existingUser.getId(), newProfileSkillsResource()
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
        Long userId = 1L;

        ServiceResult<Void> result = service.updateProfileSkills(userId, newProfileSkillsResource().build());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, userId)));

        verify(userRepositoryMock).findOne(userId);
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void updateProfileSkills_userDoesNotHaveProfileYet() throws Exception {
        Long userId = 1L;

        Profile profile = newProfile()
                .withId(23L)
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Updated")
                .build();
        User existingUser = newUser()
                .withId(userId)
                .withProfile(profile)
                .build();

        when(userRepositoryMock.findOne(userId)).thenReturn(existingUser);
        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(newProfile().build(), profile);

        ServiceResult<Void> result = service.updateProfileSkills(existingUser.getId(), newProfileSkillsResource()
                .withUser(existingUser.getId())
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Updated")
                .build());

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(profileRepositoryMock, times(2)).findOne(profile.getId());
        inOrder.verify(profileRepositoryMock).save(profile);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUserAffiliations() throws Exception {
        Long userId = 1L;

        List<Affiliation> affiliations = newAffiliation().build(2);

        List<AffiliationResource> affiliationResources = newAffiliationResource().build(2);

        when(affiliationMapperMock.mapToResource(affiliations.get(0))).thenReturn(affiliationResources.get(0));
        when(affiliationMapperMock.mapToResource(affiliations.get(1))).thenReturn(affiliationResources.get(1));

        User user = newUser()
                .withAffiliations(affiliations)
                .build();

        when(userRepositoryMock.findOne(userId)).thenReturn(user);

        List<AffiliationResource> response = service.getUserAffiliations(userId).getSuccessObject();
        assertEquals(affiliationResources, response);

        InOrder inOrder = inOrder(userRepositoryMock, affiliationMapperMock);
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(affiliationMapperMock, times(2)).mapToResource(isA(Affiliation.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getUserAffiliations_userDoesNotExist() throws Exception {
        Long userIdNotExists = 1L;

        ServiceResult<List<AffiliationResource>> response = service.getUserAffiliations(userIdNotExists);
        assertTrue(response.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findOne(userIdNotExists);
        verifyZeroInteractions(affiliationMapperMock);
    }

    @Test
    public void getUserAffiliations_noAffiliations() throws Exception {
        Long userId = 1L;

        List<Affiliation> affiliations = emptyList();

        User user = newUser()
                .withAffiliations(affiliations)
                .build();

        when(userRepositoryMock.findOne(userId)).thenReturn(user);

        List<AffiliationResource> response = service.getUserAffiliations(userId).getSuccessObject();
        assertTrue(response.isEmpty());

        verify(userRepositoryMock, only()).findOne(userId);
        verifyZeroInteractions(affiliationMapperMock);
    }

    @Test
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliationResources = newAffiliationResource().build(2);
        List<Affiliation> affiliations = newAffiliation().build(2);

        User existingUser = newUser()
                .withAffiliations(new ArrayList<>())
                .build();

        when(userRepositoryMock.findOne(userId)).thenReturn(existingUser);
        when(affiliationMapperMock.mapToDomain(affiliationResources)).thenReturn(affiliations);


        when(userRepositoryMock.save(createUserExpectations(existingUser.getId(), affiliations))).thenReturn(newUser().build());

        ServiceResult<Void> response = service.updateUserAffiliations(userId, affiliationResources);
        assertTrue(response.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, affiliationMapperMock);
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(affiliationMapperMock).mapToDomain(affiliationResources);
        inOrder.verify(userRepositoryMock).save(createUserExpectations(existingUser.getId(), affiliations));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateUserAffiliations_userDoesNotExist() throws Exception {
        Long userIdNotExists = 1L;

        ServiceResult<Void> result = service.updateUserAffiliations(userIdNotExists, new ArrayList<>());
        assertTrue(result.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findOne(userIdNotExists);
        verifyZeroInteractions(affiliationMapperMock);
    }

    @Test
    public void getProfileContract() throws Exception {
        Contract currentContract = newContract()
                .build();

        ContractResource currentContractResource = newContractResource().build();

        LocalDateTime contractSignedDate = LocalDateTime.now();

        Profile profile = newProfile()
                .withContract(currentContract)
                .withContractSignedDate(contractSignedDate)
                .build();
        User existingUser = newUser()
                .withProfile(profile)
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);
        when(contractMapperMock.mapToResource(currentContract)).thenReturn(currentContractResource);
        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileContractResource expected = newProfileContractResource()
                .withUser(existingUser.getId())
                .withContract(currentContractResource)
                .withCurrentAgreement(true)
                .withContractSignedDate(contractSignedDate)
                .build();

        ProfileContractResource response = service.getProfileContract(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(contractRepositoryMock, userRepositoryMock, contractMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(contractMapperMock).mapToResource(currentContract);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileContract_userDoesNotExist() throws Exception {
        Long userIdNotExists = 1L;

        ServiceResult<ProfileContractResource> response = service.getProfileContract(userIdNotExists);
        assertTrue(response.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findOne(userIdNotExists);
        verifyZeroInteractions(contractRepositoryMock);
        verifyZeroInteractions(contractMapperMock);
    }

    @Test
    public void getProfileContract_noAgreement() throws Exception {
        Contract currentContract = newContract()
                .build();

        ContractResource currentContractResource = newContractResource().build();

        // Profile has no contract or signed date
        User existingUser = newUser()
                .withProfile(newProfile()
                        .build())
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);
        when(contractMapperMock.mapToResource(currentContract)).thenReturn(currentContractResource);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileContractResource expected = newProfileContractResource()
                .withUser(existingUser.getId())
                .withContract(currentContractResource)
                .withCurrentAgreement(false)
                .withContractSignedDate((LocalDateTime) null)
                .build();

        ProfileContractResource response = service.getProfileContract(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(contractRepositoryMock, userRepositoryMock, contractMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(contractMapperMock).mapToResource(currentContract);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileContract_noCurrentAgreement() throws Exception {
        Contract currentContract = newContract()
                .build();

        ContractResource currentContractResource = newContractResource().build();

        // Profile has a contract and a signed date, but not the current one
        User existingUser = newUser()
                .withProfile(newProfile()
                        .withContract(newContract().build())
                        .withContractSignedDate(LocalDateTime.now())
                        .build())
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);
        when(contractMapperMock.mapToResource(currentContract)).thenReturn(currentContractResource);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileContractResource expected = newProfileContractResource()
                .withUser(existingUser.getId())
                .withContract(currentContractResource)
                .withCurrentAgreement(false)
                .withContractSignedDate((LocalDateTime) null)
                .build();

        ProfileContractResource response = service.getProfileContract(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(contractRepositoryMock, userRepositoryMock, contractMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(contractMapperMock).mapToResource(currentContract);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getProfileContract_userDoesNotHaveProfileYet() throws Exception {
        Contract currentContract = newContract()
                .build();

        ContractResource currentContractResource = newContractResource().build();

        User existingUser = newUser()
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);
        when(contractMapperMock.mapToResource(currentContract)).thenReturn(currentContractResource);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ProfileContractResource expected = newProfileContractResource()
                .withUser(existingUser.getId())
                .withContract(currentContractResource)
                .withCurrentAgreement(false)
                .withContractSignedDate((LocalDateTime) null)
                .build();

        ProfileContractResource response = service.getProfileContract(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        InOrder inOrder = inOrder(contractRepositoryMock, userRepositoryMock, contractMapperMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(contractMapperMock).mapToResource(currentContract);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileContract() throws Exception {
        LocalDateTime expectedContractSignedDate = LocalDateTime.of(2016, 10, 11, 12, 13, 14);
        setClockToTime(expectedContractSignedDate);

        Profile profile = newProfile()
                .withContract(newContract().build())
                .withContractSignedDate((LocalDateTime) null)
                .build();
        User existingUser = newUser()
                .withProfile(profile)
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);
        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);

        Contract currentContract = newContract()
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);

        when(userRepositoryMock.save(createUserExpectations(existingUser.getId(), newProfile()
                .withId(existingUser.getProfileId())
                .withContract(currentContract)
                .withContractSignedDate(expectedContractSignedDate)
                .build()))).thenReturn(newUser().build());

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findOne(profile.getId());
        inOrder.verify(profileRepositoryMock).save(profile);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileContract_userDoesNotExist() throws Exception {
        Long userIdNotExists = 1L;

        ServiceResult<Void> result = service.updateProfileContract(userIdNotExists);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, userIdNotExists)));

        verify(userRepositoryMock, only()).findOne(userIdNotExists);
        verifyZeroInteractions(contractRepositoryMock);
        verifyZeroInteractions(contractMapperMock);
    }

    @Test
    public void updateProfileContract_noAgreement() throws Exception {
        LocalDateTime expectedContractSignedDate = LocalDateTime.of(2016, 10, 11, 12, 13, 14);
        setClockToTime(expectedContractSignedDate);

        // Profile has no contract or signed date
        Profile initialProfile = newProfile()
                .build();
        User existingUser = newUser()
                .withProfile(initialProfile)
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        Contract currentContract = newContract()
                .build();
        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);

        Profile updatedProfile = newProfile()
                .with(id(existingUser.getProfileId()))
                .withContract(currentContract)
                .withContractSignedDate(expectedContractSignedDate)
                .build();
        when(profileRepositoryMock.save(updatedProfile)).thenReturn(updatedProfile);
        when(profileRepositoryMock.findOne(initialProfile.getId()))
                .thenReturn(initialProfile, initialProfile, updatedProfile);

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(profileRepositoryMock).findOne(initialProfile.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findOne(updatedProfile.getId());
        inOrder.verify(profileRepositoryMock).save(updatedProfile);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileContract_noCurrentAgreement() throws Exception {
        LocalDateTime expectedContractSignedDate = LocalDateTime.of(2016, 10, 11, 12, 13, 14);
        setClockToTime(expectedContractSignedDate);

        // Profile has a contract and a signed date, but not the current one
        Profile initialProfile = newProfile()
                .withContract(newContract().withId(1L).build())
                .withContractSignedDate(LocalDateTime.now())
                .build();
        User existingUser = newUser()
                .withProfile(initialProfile)
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        Contract currentContract = newContract()
                .withId(2L)
                .build();
        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);

        Profile updatedProfile = newProfile()
                .with(id(existingUser.getProfileId()))
                .withContract(currentContract)
                .withContractSignedDate(expectedContractSignedDate)
                .build();
        when(profileRepositoryMock.save(updatedProfile)).thenReturn(updatedProfile);
        when(profileRepositoryMock.findOne(initialProfile.getId()))
                .thenReturn(initialProfile, initialProfile, updatedProfile);

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(profileRepositoryMock).findOne(initialProfile.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findOne(updatedProfile.getId());
        inOrder.verify(profileRepositoryMock).save(updatedProfile);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileContract_agreementAlreadySigned() throws Exception {
        Contract currentContract = newContract()
                .build();
        Profile profile = newProfile()
                .withContract(currentContract)
                .withContractSignedDate(LocalDateTime.now())
                .build();
        User existingUser = newUser()
                .withProfile(profile)
                .build();
        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);
        when(profileRepositoryMock.findOne(existingUser.getProfileId())).thenReturn(profile);
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(badRequestError("validation.assessorprofiletermsform.terms.alreadysigned")));

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileContract_userDoesNotHaveProfileYet() throws Exception {
        LocalDateTime expectedContractSignedDate = LocalDateTime.of(2016, 10, 11, 12, 13, 14);
        setClockToTime(expectedContractSignedDate);

        User existingUser = newUser()
                .withId(1L)
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        Contract currentContract = newContract()
                .build();
        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);

        Profile newProfile = newProfile()
                .with(id(null))
                .build();
        Long profileId = 23L;
        Profile savedProfile = newProfile()
                .withId(profileId)
                .build();
        Profile profileWithContract = newProfile()
                .withId(profileId)
                .withContract(currentContract)
                .withContractSignedDate(expectedContractSignedDate)
                .build();
        when(profileRepositoryMock.save(newProfile)).thenReturn(savedProfile);
        when(profileRepositoryMock.findOne(profileId)).thenReturn(savedProfile, profileWithContract);

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(profileRepositoryMock).save(newProfile);
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(profileRepositoryMock, times(2)).findOne(profileId);
        inOrder.verify(profileRepositoryMock).save(profileWithContract);
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
                .withContractSignedDate(LocalDateTime.now())
                .build();
        User user = newUser()
                .withAffiliations( asList(newAffiliation().build()) )
                .withProfile(profile)
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
                        .withContractComplete(true)
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
                .withProfile(profile)
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
                        .withContractComplete(false)
                        .build(),
                result.getSuccessObject()
        );

        verify(userRepositoryMock, only()).findOne(user.getId());
    }

    @Test
    public void getUserProfileStatus_affiliationsComplete() throws Exception {
        User user = newUser()
                .withAffiliations( asList(newAffiliation().build()) )
                .build();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        ServiceResult<UserProfileStatusResource> result = service.getUserProfileStatus(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(
                newUserProfileStatusResource()
                        .withUser(user.getId())
                        .withSkillsComplete(false)
                        .withAffliliationsComplete(true)
                        .withContractComplete(false)
                        .build(),
                result.getSuccessObject()
        );

        verify(userRepositoryMock, only()).findOne(user.getId());
    }

    @Test
    public void getUserProfileStatus_contractComplete() throws Exception {
        Profile profile = newProfile()
                .withContractSignedDate(LocalDateTime.now())
                .build();
        User user = newUser()
                .withProfile(profile)
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
                        .withContractComplete(true)
                        .build(),
                result.getSuccessObject()
        );

        verify(userRepositoryMock, only()).findOne(user.getId());
    }

    @Test
    public void getUserProfile() {
        Profile existingProfile = newProfile()
                .withAddress(newAddress().withId(1L).build())
                .withContract(newContract().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .build();
        User existingUser = newUser()
                .withProfile(existingProfile)
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
                .withProfile(originalProfile)
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
        inOrder.verify(profileRepositoryMock, times(2)).findOne(originalProfile.getId());
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

    private void setClockToTime(LocalDateTime time) {
        Clock clock = Clock.fixed(time.atZone(systemDefault()).toInstant(), systemDefault());
        ReflectionTestUtils.setField(service, "clock", clock, Clock.class);
    }
}
