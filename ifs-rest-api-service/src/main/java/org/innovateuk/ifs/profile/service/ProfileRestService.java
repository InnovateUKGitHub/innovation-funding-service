package org.innovateuk.ifs.profile.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.*;

/**
 * Interface for CRUD operations on {@link UserProfileResource} related data.
 */
public interface ProfileRestService {
    RestResult<ProfileSkillsResource> getProfileSkills(Long userId);
    RestResult<Void> updateProfileSkills(Long userId, ProfileSkillsEditResource profileSkills);
    RestResult<ProfileAgreementResource> getProfileAgreement(Long userId);
    RestResult<Void> updateProfileAgreement(Long userId);
    RestResult<UserProfileResource> getUserProfile(Long userId);
    RestResult<Void> updateUserProfile(Long userId, UserProfileResource userProfile);
    RestResult<UserProfileStatusResource> getUserProfileStatus(Long userId);
}
