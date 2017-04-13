package org.innovateuk.ifs.profile.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProfileRestServiceImplTest extends BaseRestServiceUnitTest<ProfileRestServiceImpl> {

    private static final String profileUrl = "/profile";

    @Override
    protected ProfileRestServiceImpl registerRestServiceUnderTest() {
        ProfileRestServiceImpl profileRestService = new ProfileRestServiceImpl();
        return profileRestService;
    }

    @Test
    public void getProfileSkills() {
        Long userId = 1L;
        ProfileSkillsResource expected = newProfileSkillsResource().build();

        setupGetWithRestResultExpectations(format("%s/id/%s/getProfileSkills", profileUrl, userId), ProfileSkillsResource.class, expected, OK);

        ProfileSkillsResource response = service.getProfileSkills(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }

    @Test
    public void updateProfileSkills() {
        Long userId = 1L;
        ProfileSkillsEditResource profileSkillsEditResource = newProfileSkillsEditResource().build();

        setupPutWithRestResultExpectations(format("%s/id/%s/updateProfileSkills", profileUrl, userId), profileSkillsEditResource, OK);

        RestResult<Void> response = service.updateProfileSkills(userId, profileSkillsEditResource);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getProfileAgreement() {
        Long userId = 1L;
        ProfileAgreementResource expected = newProfileAgreementResource().build();

        setupGetWithRestResultExpectations(format("%s/id/%s/getProfileAgreement", profileUrl, userId), ProfileAgreementResource.class, expected, OK);

        ProfileAgreementResource response = service.getProfileAgreement(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }

    @Test
    public void updateProfileAgreement() {
        Long userId = 1L;

        setupPutWithRestResultExpectations(format("%s/id/%s/updateProfileAgreement", profileUrl, userId), null, OK);

        RestResult<Void> response = service.updateProfileAgreement(userId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getProfileAddress() {
        Long userId = 1L;
        UserProfileResource expected = newUserProfileResource().build();

        setupGetWithRestResultExpectations(format("%s/id/%s/getUserProfile", profileUrl, userId), UserProfileResource.class, expected, OK);

        UserProfileResource response = service.getUserProfile(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }

    @Test
    public void updateProfileAddress() {
        Long userId = 1L;
        UserProfileResource profileDetails = newUserProfileResource().build();

        setupPutWithRestResultExpectations(format("%s/id/%s/updateUserProfile", profileUrl, userId), profileDetails, OK);

        RestResult<Void> response = service.updateUserProfile(userId, profileDetails);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getProfileStatus() {
        Long userId = 1L;
        UserProfileStatusResource expected = newUserProfileStatusResource().build();

        setupGetWithRestResultExpectations(format("%s/id/%s/profileStatus", profileUrl, userId), UserProfileStatusResource.class, expected, OK);

        UserProfileStatusResource response = service.getUserProfileStatus(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }
}
