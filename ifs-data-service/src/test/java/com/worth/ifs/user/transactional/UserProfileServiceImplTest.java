package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.resource.*;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.ContractBuilder.newContract;
import static com.worth.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static com.worth.ifs.user.builder.EthnicityBuilder.newEthnicity;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.user.builder.ProfileBuilder.newProfile;
import static com.worth.ifs.user.builder.ProfileContractResourceBuilder.newProfileContractResource;
import static com.worth.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static com.worth.ifs.user.resource.BusinessType.ACADEMIC;
import static com.worth.ifs.user.resource.BusinessType.BUSINESS;
import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
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
                .withUser(existingUser)
                .withAddress(newAddress().build())
                .withContract(newContract().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .build();
        existingUser.setProfile(profile);

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

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
    public void getProfileSkills_noSkills() {
        User existingUser = newUser().build();
        Profile profile = newProfile()
                .withUser(existingUser)
                .withAddress(newAddress().build())
                .withContract(newContract().build())
                .build();
        existingUser.setProfile(profile);

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

        User existingUser = newUser().build();
        Profile profile = newProfile()
                .withUser(existingUser)
                .withAddress(newAddress().build())
                .withContract(newContract().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .build();
        existingUser.setProfile(profile);

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        User expectedUser = createLambdaMatcher(
                user -> {
                    assertEquals(userId, user.getId());
                    assertEquals(existingUser.getProfile().getId(), user.getProfile().getId());
                    assertEquals(existingUser.getProfile().getUser(), user.getProfile().getUser().getId());
                    assertEquals(existingUser.getProfile().getAddress(), user.getProfile().getAddress());
                    assertEquals(existingUser.getProfile().getContract(), user.getProfile().getContract());
                    assertEquals(BUSINESS, user.getProfile().getBusinessType());
                    assertEquals("Updated", user.getProfile().getSkillsAreas());
                }
        );

        when(userRepositoryMock.save(expectedUser)).thenReturn(newUser().build());

        ServiceResult<Void> result = service.updateProfileSkills(existingUser.getId(), newProfileSkillsResource()
                .withUser(existingUser.getId())
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Skills")
                .build());

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(userRepositoryMock).save(isA(User.class));
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
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliationResources = newAffiliationResource().build(2);
        List<Affiliation> affiliations = newAffiliation().build(2);

        User existingUser = newUser()
                .withAffiliations(new ArrayList<>())
                .build();

        when(userRepositoryMock.findOne(userId)).thenReturn(existingUser);
        when(affiliationMapperMock.mapToDomain(affiliationResources)).thenReturn(affiliations);

        User userWithAffiliationsExpectation = createLambdaMatcher(user -> {
            assertEquals(affiliations, user.getAffiliations());
        });

        when(userRepositoryMock.save(userWithAffiliationsExpectation)).thenReturn(newUser().build());

        ServiceResult<Void> response = service.updateUserAffiliations(userId, affiliationResources);
        assertTrue(response.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, affiliationMapperMock);
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(affiliationMapperMock).mapToDomain(affiliationResources);
        inOrder.verify(userRepositoryMock).save(isA(User.class));
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
    public void getProfileContract() throws Exception {
        Contract currentContract = newContract()
                .build();

        ContractResource currentContractResource = newContractResource().build();

        LocalDateTime contractSignedDate = LocalDateTime.now();

        User existingUser = newUser()
                .withProfile(newProfile()
                        .withContract(currentContract)
                        .withContractSignedDate(contractSignedDate)
                        .build())
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);
        when(contractMapperMock.mapToResource(currentContract)).thenReturn(currentContractResource);
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
    public void updateProfileContract() throws Exception {
        LocalDateTime expectedContractSignedDate = LocalDateTime.of(2016, 10, 11, 12, 13, 14);
        setClockToTime(expectedContractSignedDate);

        User existingUser = newUser()
                .withProfile(newProfile()
                        .withContract(newContract().build())
                        .withContractSignedDate((LocalDateTime) null)
                        .build())
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        Contract currentContract = newContract()
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);

        User expectedUser = createLambdaMatcher(user -> {
            assertEquals(existingUser.getId(), user.getId());
            assertEquals(existingUser.getProfile().getId(), user.getProfile().getId());
            assertEquals(currentContract, user.getProfile().getContract());
            assertEquals(expectedContractSignedDate, user.getProfile().getContractSignedDate());
        });


        when(userRepositoryMock.save(expectedUser)).thenReturn(newUser().build());

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(userRepositoryMock).save(isA(User.class));
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
        User existingUser = newUser()
                .withProfile(newProfile()
                        .build())
                .build();

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        Contract currentContract = newContract()
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);

        User expectedUser = createLambdaMatcher(user -> {
            assertEquals(existingUser.getId(), user.getId());
            assertEquals(existingUser.getProfile().getId(), user.getProfile().getId());
            assertEquals(currentContract, user.getProfile().getContract());
            assertEquals(expectedContractSignedDate, user.getProfile().getContractSignedDate());
        });


        when(userRepositoryMock.save(expectedUser)).thenReturn(newUser().build());

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(userRepositoryMock).save(isA(User.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileContract_noCurrentAgreement() throws Exception {
        LocalDateTime expectedContractSignedDate = LocalDateTime.of(2016, 10, 11, 12, 13, 14);
        setClockToTime(expectedContractSignedDate);

        // Profile has a contract and a signed date, but not the current one
        User existingUser = newUser()
                .withProfile(newProfile()
                        .withContract(newContract().build())
                        .withContractSignedDate(LocalDateTime.now())
                        .build())
                .build();

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        Contract currentContract = newContract()
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);

        User expectedUser = createLambdaMatcher(user -> {
            assertEquals(existingUser.getId(), user.getId());
            assertEquals(existingUser.getProfile().getId(), user.getProfile().getId());
            assertEquals(currentContract, user.getProfile().getContract());
            assertEquals(expectedContractSignedDate, user.getProfile().getContractSignedDate());
        });


        when(userRepositoryMock.save(expectedUser)).thenReturn(newUser().build());

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(userRepositoryMock).save(isA(User.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateProfileContract_agreementAlreadySigned() throws Exception {
        Contract currentContract = newContract()
                .build();

        User existingUser = newUser()
                .withProfile(newProfile()
                        .withContract(currentContract)
                        .withContractSignedDate(LocalDateTime.now())
                        .build())
                .build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(badRequestError("validation.assessorprofiletermsform.terms.alreadysigned")));

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateUserContract_userDoesNotHaveProfileYet() throws Exception {
        LocalDateTime expectedContractSignedDate = LocalDateTime.of(2016, 10, 11, 12, 13, 14);
        setClockToTime(expectedContractSignedDate);

        User existingUser = newUser().build();
        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        Contract currentContract = newContract()
                .build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(currentContract);

        User expectedUser = createLambdaMatcher(user -> {
            assertEquals(existingUser.getId(), user.getId());
            assertNotNull(existingUser.getProfile());
            assertEquals(currentContract, user.getProfile().getContract());
            assertEquals(expectedContractSignedDate, user.getProfile().getContractSignedDate());
        });


        when(userRepositoryMock.save(expectedUser)).thenReturn(newUser().build());

        ServiceResult<Void> result = service.updateProfileContract(existingUser.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, contractRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingUser.getId());
        inOrder.verify(contractRepositoryMock).findByCurrentTrue();
        inOrder.verify(userRepositoryMock).save(isA(User.class));
        inOrder.verifyNoMoreInteractions();
    }

    private void setClockToTime(LocalDateTime time) {
        Clock clock = Clock.fixed(time.atZone(systemDefault()).toInstant(), systemDefault());
        ReflectionTestUtils.setField(service, "clock", clock, Clock.class);
    }

    @Test
    public void testGetUserProfileDetails() {
        User existingUser = newUser().build();

        Profile profile = newProfile()
                .withUser(existingUser)
                .withAddress(newAddress().withId(1L).build())
                .withContract(newContract().build())
                .withBusinessType(ACADEMIC)
                .withSkillsAreas("Skills")
                .build();
        existingUser.setProfile(profile);

        AddressResource addressResource = newAddressResource().withId(1L).build();

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);
        when(addressMapperMock.mapToResource(profile.getAddress())).thenReturn(addressResource);
        EthnicityResource ethnicity = newEthnicityResource().build();
        when(ethnicityMapperMock.mapToResource(newEthnicity().build())).thenReturn(ethnicity);

        UserProfileResource expected = newUserProfileResource()
                .withUser(existingUser.getId())
                .withFirstName(existingUser.getFirstName())
                .withLastName(existingUser.getLastName())
                .withEmail(existingUser.getEmail())
                .withAddress(addressResource)
                .build();

        UserProfileResource response = service.getUserProfile(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        verify(userRepositoryMock).findOne(existingUser.getId());
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void testUpdateProfileDetails() {
        Long userId = 1L;

        User existingUser = newUser().build();
        Profile profile = newProfile()
                .withUser(existingUser)
                .withAddress(newAddress().build())
                .build();
        existingUser.setProfile(profile);
        Ethnicity ethnicity = newEthnicity().withId(1L).build();
        existingUser.setEthnicity(ethnicity);

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        User expectedUser = createLambdaMatcher(
                user -> {
                    assertEquals(userId, user.getId());
                    assertEquals(existingUser.getProfile().getId(), user.getProfile().getId());
                    assertEquals(existingUser.getProfile().getUser(), user.getProfile().getUser().getId());
                    assertEquals(existingUser.getProfile().getAddress(), user.getProfile().getAddress());
                }
        );

        EthnicityResource ethnicityResource = newEthnicityResource().build();
        when(userRepositoryMock.save(expectedUser)).thenReturn(newUser().build());
        AddressResource address = newAddressResource().build();
        when(addressMapperMock.mapToDomain(address)).thenReturn(newAddress().build());
        UserProfileResource userDetails = newUserProfileResource()
                .withEthnicity(ethnicityResource)
                .withAddress(address)
                .build();

        ServiceResult<Void> result = service.updateUserProfile(existingUser.getId(), userDetails);

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(userRepositoryMock).save(isA(User.class));
        inOrder.verifyNoMoreInteractions();
    }
}
