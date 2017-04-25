package org.innovateuk.ifs.profile.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * A Service that covers basic operations concerning Profiles
 */
public interface ProfileService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProfileSkillsResource> getProfileSkills(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateProfileSkills(long userId, ProfileSkillsEditResource profileResource);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProfileAgreementResource> getProfileAgreement(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateProfileAgreement(long userId);

    @PostAuthorize("hasPermission(returnObject, 'READ_USER_PROFILE')")
    ServiceResult<UserProfileResource> getUserProfile(Long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateUserProfile(Long userId, UserProfileResource profileDetails);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<UserProfileStatusResource> getUserProfileStatus(Long userId);
}
