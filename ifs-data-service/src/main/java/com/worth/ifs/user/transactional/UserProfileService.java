package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.resource.UserResource;

/**
 * A Service for operations regarding Users' profiles
 */
public interface UserProfileService {

    @NotSecured("TODO implement when permissions matrix available")
    ServiceResult<Void> updateProfile(UserResource userResource);
}
