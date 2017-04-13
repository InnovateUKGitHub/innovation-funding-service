package org.innovateuk.ifs.profile.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.innovateuk.ifs.user.resource.UserProfileResource;

/**
 * Interface for CRUD operations on {@link ProfileSkillsResource} related data.
 */
public interface ProfileService {
    ProfileSkillsResource getProfileSkills(Long userId);
    ServiceResult<Void> updateProfileSkills(Long userId, BusinessType businessType, String skillsAreas);
    ProfileAgreementResource getProfileAgreement(Long userId);
    ServiceResult<Void> updateProfileAgreement(Long userId);
    UserProfileResource getUserProfile(Long userId);
    ServiceResult<Void> updateUserProfile(Long userId, UserProfileResource userProfile);
}
