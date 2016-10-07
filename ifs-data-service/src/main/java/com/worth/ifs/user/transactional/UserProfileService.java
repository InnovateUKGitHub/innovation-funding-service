package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * A Service for operations regarding Users' profiles
 */
public interface UserProfileService {

    @PreAuthorize("hasPermission(#userId, 'com.worth.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateProfile(Long userId, ProfileResource profileResource);

    @PreAuthorize("hasPermission(#userBeingUpdated, 'UPDATE')")
    ServiceResult<Void> updateDetails(@P("userBeingUpdated") UserResource userBeingUpdated);
}
