package com.worth.ifs.user.transactional;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

/**
 * A Service for operations regarding Users' profiles
 */
public interface UserProfileService {

    @NotSecured("TODO implement when permissions matrix available")
    ServiceResult<User> updateProfile(UserResource userResource);
}
