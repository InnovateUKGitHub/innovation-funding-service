package com.worth.ifs.user.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.error.exception.GeneralUnexpectedErrorException;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.ProfileContractResourceBuilder.newProfileContractResource;
import static com.worth.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static com.worth.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static com.worth.ifs.user.resource.BusinessType.BUSINESS;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


/**
 * Test Class for functionality in {@link UserServiceImpl}
 */
public class UserServiceImplTest extends BaseServiceUnitTest<UserService> {
    private static final String EMAIL_THAT_EXISTS_FOR_USER = "sample@me.com";

    private static final String EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR = "i-am-bad@me.com";
    @Mock
    private UserRestService userRestService;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        when(userRestService.resendEmailVerificationNotification(eq(EMAIL_THAT_EXISTS_FOR_USER))).thenReturn(restSuccess());
        when(userRestService.resendEmailVerificationNotification(eq(EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR))).thenReturn(restFailure(internalServerErrorError()));
        when(userRestService.resendEmailVerificationNotification(not(or(eq(EMAIL_THAT_EXISTS_FOR_USER), eq(EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR))))).thenReturn(restFailure(notFoundError(UserResource.class)));
    }

    @Override
    protected UserService supplyServiceUnderTest() {
        return new UserServiceImpl();
    }

    @Test
    public void resendEmailVerificationNotification() throws Exception {
        service.resendEmailVerificationNotification(EMAIL_THAT_EXISTS_FOR_USER);
        verify(userRestService, only()).resendEmailVerificationNotification(EMAIL_THAT_EXISTS_FOR_USER);
    }

    @Test(expected = Test.None.class /* No exception expected here even though the mock returns an ObjectNotFoundException. We don't want to reveal that an email address was not recognised. */)
    public void resendEmailVerificationNotification_notExists() throws Exception {
        // Try sending the verification link to an email address which doesn't exist for a user
        final String email = "i-dont-exist@me.com";

        service.resendEmailVerificationNotification(email);
        verify(userRestService, only()).resendEmailVerificationNotification(email);
    }

    @Test(expected = GeneralUnexpectedErrorException.class)
    public void resendEmailVerificationNotification_otherError() throws Exception {
        // Try sending the verification link to an email address that exists but cause another error to occur

        service.resendEmailVerificationNotification(EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR);
        verify(userRestService, only()).resendEmailVerificationNotification(EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR);
    }

    @Test
    public void findUserByType() throws Exception {
        UserResource userOne = new UserResource();
        userOne.setId(1L);

        UserResource userTwo = new UserResource();
        userTwo.setId(2L);

        List<UserResource> expected = new ArrayList<>(asList(userOne, userTwo));
        when(userRestService.findByUserRoleType(UserRoleType.COMP_EXEC)).thenReturn(restSuccess(expected));

        List<UserResource> found = service.findUserByType(UserRoleType.COMP_EXEC);
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1), found.get(0).getId());
        assertEquals(Long.valueOf(2), found.get(1).getId());
    }

    @Test
    public void getProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsResource expected = newProfileSkillsResource().build();

        when(userRestService.getProfileSkills(userId)).thenReturn(restSuccess(expected));

        ProfileSkillsResource response = service.getProfileSkills(userId);
        assertSame(expected, response);
        verify(userRestService, only()).getProfileSkills(userId);
    }

    @Test
    public void updateProfileSkills() throws Exception {
        Long userId = 1L;
        BusinessType businessType = BUSINESS;
        String skillsAreas = "Skills";

        ProfileSkillsResource profileSkills = newProfileSkillsResource()
                .withBusinessType(businessType)
                .withSkillsAreas(skillsAreas)
                .build();

        when(userRestService.updateProfileSkills(userId, profileSkills)).thenReturn(restSuccess());

        service.updateProfileSkills(userId, businessType, skillsAreas).getSuccessObjectOrThrowException();
        verify(userRestService, only()).updateProfileSkills(userId, profileSkills);
    }

    @Test
    public void getProfileContract() throws Exception {
        Long userId = 1L;
        ProfileContractResource expected = newProfileContractResource().build();

        when(userRestService.getProfileContract(userId)).thenReturn(restSuccess(expected));

        ProfileContractResource response = service.getProfileContract(userId);
        assertSame(expected, response);
        verify(userRestService, only()).getProfileContract(userId);
    }

    @Test
    public void updateProfileContract() throws Exception {
        Long userId = 1L;

        when(userRestService.updateProfileContract(userId)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.updateProfileContract(userId);
        assertTrue(response.isSuccess());

        verify(userRestService, only()).updateProfileContract(userId);
    }

    @Test
    public void getUserAffilliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> expected = newAffiliationResource().build(2);

        when(userRestService.getUserAffiliations(userId)).thenReturn(restSuccess(expected));

        List<AffiliationResource> response = service.getUserAffiliations(userId);
        assertSame(expected, response);
        verify(userRestService, only()).getUserAffiliations(userId);
    }

    @Test
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);

        when(userRestService.updateUserAffiliations(userId, affiliations)).thenReturn(restSuccess());

        service.updateUserAffiliations(userId, affiliations).getSuccessObjectOrThrowException();
        verify(userRestService, only()).updateUserAffiliations(userId, affiliations);
    }

    @Test
    public void getProfileDetails() throws Exception {
        Long userId = 1L;
        UserProfileResource expected = newUserProfileResource().build();

        when(userRestService.getProfileDetails(userId)).thenReturn(restSuccess(expected));

        UserProfileResource response = service.getProfileDetails(userId);
        assertSame(expected, response);
        verify(userRestService, only()).getProfileDetails(userId);
    }

    @Test
    public void updateProfileDetails() throws Exception {
        Long userId = 1L;

        UserProfileResource profileDetails = newUserProfileResource().build();
        when(userRestService.updateProfileDetails(userId, profileDetails)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.updateProfileDetails(userId, profileDetails);
        assertTrue(response.isSuccess());

        verify(userRestService, only()).updateProfileDetails(userId, profileDetails);
    }
}