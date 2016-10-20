package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.Affiliation;
import com.worth.ifs.user.domain.Profile;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.ProfileAddressResource;
import com.worth.ifs.user.resource.ProfileSkillsResource;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.ContractBuilder.newContract;
import static com.worth.ifs.user.builder.ProfileAddressResourceBuilder.newProfileAddressResource;
import static com.worth.ifs.user.builder.ProfileBuilder.newProfile;
import static com.worth.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.resource.BusinessType.ACADEMIC;
import static com.worth.ifs.user.resource.BusinessType.BUSINESS;
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
    public void testGetUserProfileSkills() {
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
    public void testUpdateProfileSkills() {
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
    public void testUpdateProfileSkillsButUserNotFound() {
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
    public void testGetUserProfileAddress() {
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

        ProfileAddressResource expected = newProfileAddressResource()
                .withUser(existingUser.getId())
                .withAddress(newAddressResource().withId(1L).build())
                .build();

        ProfileAddressResource response = service.getProfileAddress(existingUser.getId()).getSuccessObject();
        assertEquals(expected, response);

        verify(userRepositoryMock).findOne(existingUser.getId());
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void testUpdateProfileAddress() {
        Long userId = 1L;

        User existingUser = newUser().build();
        Profile profile = newProfile()
                .withUser(existingUser)
                .withAddress(newAddress().build())

                .build();
        existingUser.setProfile(profile);

        when(userRepositoryMock.findOne(existingUser.getId())).thenReturn(existingUser);

        User expectedUser = createLambdaMatcher(
                user -> {
                    assertEquals(userId, user.getId());
                    assertEquals(existingUser.getProfile().getId(), user.getProfile().getId());
                    assertEquals(existingUser.getProfile().getUser(), user.getProfile().getUser().getId());
                    assertEquals(existingUser.getProfile().getAddress(), user.getProfile().getAddress());
                }
        );

        when(userRepositoryMock.save(expectedUser)).thenReturn(newUser().build());
        ProfileAddressResource addressResource = newProfileAddressResource().build();
        when(addressMapperMock.mapToDomain(addressResource.getAddress())).thenReturn(newAddress().build());

        ServiceResult<Void> result = service.updateProfileAddress(existingUser.getId(), addressResource);

        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(userId);
        inOrder.verify(userRepositoryMock).save(isA(User.class));
        inOrder.verifyNoMoreInteractions();
    }
}
