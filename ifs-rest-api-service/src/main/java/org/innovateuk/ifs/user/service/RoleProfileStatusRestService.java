package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;

import java.util.List;


public interface RoleProfileStatusRestService {

    RestResult<Void> updateUserStatus(long userId, RoleProfileStatusResource roleProfileStatusResource);

    RestResult<List<RoleProfileStatusResource>> findByUserId(long userId);

    RestResult<RoleProfileStatusResource> findByUserIdAndProfileRole(long userId, ProfileRole profileRole);
}