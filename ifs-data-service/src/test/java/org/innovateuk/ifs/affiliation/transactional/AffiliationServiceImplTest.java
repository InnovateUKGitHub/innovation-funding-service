package org.innovateuk.ifs.affiliation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.domain.Ethnicity;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verifyZeroInteractions;

public class AffiliationServiceImplTest extends BaseServiceUnitTest<AffiliationServiceImpl> {

    @Override
    protected AffiliationServiceImpl supplyServiceUnderTest() {
        return new AffiliationServiceImpl();
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
