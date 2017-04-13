package org.innovateuk.ifs.profile.service;


import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Class for functionality in {@link ProfileServiceImpl}
 */
public class ProfileServiceImplTest extends BaseServiceUnitTest<ProfileService> {

    @Mock
    private ProfileRestService profileRestService;

    @Override
    protected ProfileService supplyServiceUnderTest() {
        return new ProfileServiceImpl();
    }

    @Test
    public void getProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsResource expected = newProfileSkillsResource().build();

        when(profileRestService.getProfileSkills(userId)).thenReturn(restSuccess(expected));

        ProfileSkillsResource response = service.getProfileSkills(userId);
        assertSame(expected, response);
        verify(profileRestService, only()).getProfileSkills(userId);
    }

    @Test
    public void updateProfileSkills() throws Exception {
        Long userId = 1L;
        BusinessType businessType = BUSINESS;
        String skillsAreas = "Skills";

        ProfileSkillsEditResource profileSkillsEditResource = newProfileSkillsEditResource()
                .withBusinessType(businessType)
                .withSkillsAreas(skillsAreas)
                .build();

        when(profileRestService.updateProfileSkills(userId, profileSkillsEditResource)).thenReturn(restSuccess());

        service.updateProfileSkills(userId, businessType, skillsAreas).getSuccessObjectOrThrowException();
        verify(profileRestService, only()).updateProfileSkills(userId, profileSkillsEditResource);
    }

    @Test
    public void getProfileAgreement() throws Exception {
        Long userId = 1L;
        ProfileAgreementResource expected = newProfileAgreementResource().build();

        when(profileRestService.getProfileAgreement(userId)).thenReturn(restSuccess(expected));

        ProfileAgreementResource response = service.getProfileAgreement(userId);
        assertSame(expected, response);
        verify(profileRestService, only()).getProfileAgreement(userId);
    }

    @Test
    public void updateProfileAgreement() throws Exception {
        Long userId = 1L;

        when(profileRestService.updateProfileAgreement(userId)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.updateProfileAgreement(userId);
        assertTrue(response.isSuccess());

        verify(profileRestService, only()).updateProfileAgreement(userId);
    }

    @Test
    public void getProfileDetails() throws Exception {
        Long userId = 1L;
        UserProfileResource expected = newUserProfileResource().build();

        when(profileRestService.getUserProfile(userId)).thenReturn(restSuccess(expected));

        UserProfileResource response = service.getUserProfile(userId);
        assertSame(expected, response);
        verify(profileRestService, only()).getUserProfile(userId);
    }

    @Test
    public void updateProfileDetails() throws Exception {
        Long userId = 1L;

        UserProfileResource profileDetails = newUserProfileResource().build();
        when(profileRestService.updateUserProfile(userId, profileDetails)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.updateUserProfile(userId, profileDetails);
        assertTrue(response.isSuccess());

        verify(profileRestService, only()).updateUserProfile(userId, profileDetails);
    }
}
