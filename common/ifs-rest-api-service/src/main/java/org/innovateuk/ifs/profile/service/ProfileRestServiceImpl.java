package org.innovateuk.ifs.profile.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * ProfileRestServiceImpl is a utility for CRUD operations on {@link ProfileSkillsResource}.
 * This class connects to the {org.innovateuk.ifs.profile.controller.ProfileController}
 * through a REST call.
 */
@Service
public class ProfileRestServiceImpl extends BaseRestService implements ProfileRestService {

    private String profileRestURL = "/profile";

    @Override
    public RestResult<ProfileSkillsResource> getProfileSkills(Long userId) {
        return getWithRestResult(format("%s/id/%s/get-profile-skills", profileRestURL, userId), ProfileSkillsResource.class);
    }

    @Override
    public RestResult<Void> updateProfileSkills(Long userId, ProfileSkillsEditResource profileSkillsEditResource) {
        return putWithRestResult(format("%s/id/%s/update-profile-skills", profileRestURL, userId), profileSkillsEditResource, Void.class);
    }

    @Override
    public RestResult<ProfileAgreementResource> getProfileAgreement(Long userId) {
        return getWithRestResult(format("%s/id/%s/get-profile-agreement", profileRestURL, userId), ProfileAgreementResource.class);
    }

    @Override
    public RestResult<Void> updateProfileAgreement(Long userId) {
        return putWithRestResult(format("%s/id/%s/update-profile-agreement", profileRestURL, userId), Void.class);
    }

    @Override
    public RestResult<UserProfileResource> getUserProfile(Long userId) {
        return getWithRestResult(format("%s/id/%s/get-user-profile", profileRestURL, userId), UserProfileResource.class);
    }

    @Override
    public RestResult<Void> updateUserProfile(Long userId, UserProfileResource userProfile) {
        return putWithRestResult(format("%s/id/%s/update-user-profile", profileRestURL, userId), userProfile, Void.class);
    }

    @Override
    public RestResult<UserProfileStatusResource> getUserProfileStatus(Long userId) {
        return getWithRestResult(format("%s/id/%s/profile-status", profileRestURL, userId), UserProfileStatusResource.class);
    }
}
