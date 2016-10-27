package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.*;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.util.List;

/**
 * A Service for operations regarding Users' profiles
 */
public interface UserProfileService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProfileSkillsResource> getProfileSkills(Long userId);

    @PreAuthorize("hasPermission(#userId, 'com.worth.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateProfileSkills(Long userId, ProfileSkillsResource profileResource);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProfileContractResource> getProfileContract(Long userId);

    @PreAuthorize("hasPermission(#userId, 'com.worth.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateProfileContract(Long userId);

    @PreAuthorize("hasPermission(#userBeingUpdated, 'UPDATE')")
    ServiceResult<Void> updateDetails(@P("userBeingUpdated") UserResource userBeingUpdated);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<AffiliationResource>> getUserAffiliations(Long userId);

    @PreAuthorize("hasPermission(#userId, 'com.worth.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations);

    @PostAuthorize("hasPermission(returnObject, 'READ_USER_PROFILE')")
    ServiceResult<UserProfileResource> getUserProfile(Long userId);

    @PreAuthorize("hasPermission(#userId, 'com.worth.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateUserProfile(Long userId, UserProfileResource profileDetails);
}
