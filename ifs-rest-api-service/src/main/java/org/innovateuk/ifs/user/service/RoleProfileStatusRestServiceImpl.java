package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static java.lang.String.format;

@Service
public class RoleProfileStatusRestServiceImpl extends BaseRestService implements RoleProfileStatusRestService {

    private static final String ROLE_PROFILE_STATUS_REST_URL = "/user/%d/role-profile-status";
    private static final String USER_REST_URL = "/user";

    @Override
    public RestResult<Void> updateUserStatus(long userId, RoleProfileStatusResource roleProfileStatusResource) {
        return putWithRestResult(format(ROLE_PROFILE_STATUS_REST_URL, userId), roleProfileStatusResource, Void.class);
    }

    @Override
    public RestResult<List<RoleProfileStatusResource>> findByUserId(long userId) {
        return getWithRestResult(format(ROLE_PROFILE_STATUS_REST_URL, userId), new ParameterizedTypeReference<List<RoleProfileStatusResource>>() {});
    }

    @Override
    public RestResult<RoleProfileStatusResource> findByUserIdAndProfileRole(long userId, ProfileRole profileRole) {
        return getWithRestResult(format(ROLE_PROFILE_STATUS_REST_URL, userId) + "/" + profileRole.name(), RoleProfileStatusResource.class);
    }

    @Override
    public RestResult<UserPageResource> getAvailableAssessors(String filter, int pageNumber, int pageSize) {
        return getAssessorsWithProfileState(RoleProfileState.ACTIVE, filter, pageNumber, pageSize);
    }

    @Override
    public RestResult<UserPageResource> getUnavailableAssessors(String filter, int pageNumber, int pageSize) {
        return getAssessorsWithProfileState(RoleProfileState.UNAVAILABLE, filter, pageNumber, pageSize);
    }

    @Override
    public RestResult<UserPageResource> getDisabledAssessors(String filter, int pageNumber, int pageSize) {
        return getAssessorsWithProfileState(RoleProfileState.DISABLED, filter, pageNumber, pageSize);
    }

    private RestResult<UserPageResource> getAssessorsWithProfileState(RoleProfileState state, String filter, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);
        String uriWithParams = buildPaginationUri(String.format("%s/role-profile-status/%s/%s", USER_REST_URL, state, ProfileRole.ASSESSOR),  pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, UserPageResource.class);
    }
}