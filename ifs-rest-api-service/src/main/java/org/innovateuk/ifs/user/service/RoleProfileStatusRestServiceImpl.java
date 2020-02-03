package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@Service
public class RoleProfileStatusRestServiceImpl extends BaseRestService implements RoleProfileStatusRestService {

    private static final String USER_REST_URL = "/user";

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