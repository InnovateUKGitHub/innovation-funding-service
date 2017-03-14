package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * A Service for operations regarding Users' profiles
 */
public interface UserProfileService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProfileSkillsResource> getProfileSkills(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateProfileSkills(long userId, ProfileSkillsEditResource profileResource);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProfileAgreementResource> getProfileAgreement(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateProfileAgreement(long userId);

    @PreAuthorize("hasPermission(#userBeingUpdated, 'UPDATE')")
    ServiceResult<Void> updateDetails(@P("userBeingUpdated") UserResource userBeingUpdated);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<AffiliationResource>> getUserAffiliations(Long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations);

    @PostAuthorize("hasPermission(returnObject, 'READ_USER_PROFILE')")
    ServiceResult<UserProfileResource> getUserProfile(Long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateUserProfile(Long userId, UserProfileResource profileDetails);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<UserProfileStatusResource> getUserProfileStatus(Long userId);
}
