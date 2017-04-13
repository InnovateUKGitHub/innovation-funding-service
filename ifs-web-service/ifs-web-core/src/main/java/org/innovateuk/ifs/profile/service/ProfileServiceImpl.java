package org.innovateuk.ifs.profile.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link UserProfileResource} related data,
 * through the RestService {@link ProfileRestService}.
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRestService profileRestService;

    @Override
    public ProfileSkillsResource getProfileSkills(Long userId) {
        return profileRestService.getProfileSkills(userId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> updateProfileSkills(Long userId, BusinessType businessType, String skillsAreas) {
        ProfileSkillsEditResource profileSkillsEditResource = new ProfileSkillsEditResource();
        profileSkillsEditResource.setBusinessType(businessType);
        profileSkillsEditResource.setSkillsAreas(skillsAreas);
        return profileRestService.updateProfileSkills(userId, profileSkillsEditResource).toServiceResult();
    }

    @Override
    public UserProfileResource getUserProfile(Long userId) {
        return profileRestService.getUserProfile(userId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> updateUserProfile(Long userId, UserProfileResource userProfile) {
        return profileRestService.updateUserProfile(userId, userProfile).toServiceResult();
    }

    @Override
    public ProfileAgreementResource getProfileAgreement(Long userId) {
        return profileRestService.getProfileAgreement(userId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> updateProfileAgreement(Long userId) {
        return profileRestService.updateProfileAgreement(userId).toServiceResult();
    }
}
