package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
public class RoleProfileStatusRestServiceImpl extends BaseRestService implements RoleProfileStatusRestService {

    private String restUrl = "/user/%d/role-profile-status";

    @Override
    public RestResult<Void> updateUserStatus(long userId, RoleProfileStatusResource roleProfileStatusResource) {
        return putWithRestResult(format(restUrl, userId), roleProfileStatusResource, Void.class);
    }

    @Override
    public RestResult<List<RoleProfileStatusResource>> findByUserId(long userId) {
        return getWithRestResult(format(restUrl, userId), new ParameterizedTypeReference<List<RoleProfileStatusResource>>() {});
    }

    @Override
    public RestResult<RoleProfileStatusResource> findByUserIdAndProfileRole(long userId, ProfileRole profileRole) {
        return getWithRestResult(format(restUrl, userId) + "/" + profileRole.name(), RoleProfileStatusResource.class);
    }
}
