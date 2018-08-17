package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.externalInviteResourceListType;

/**
 * A typical RestService to use as a client API on the web-service side for the data-service functionality.
 *
 * REST service for Invite User
 */
@Service
public class InviteUserRestServiceImpl extends BaseRestService implements InviteUserRestService {
    private static final String INVITE_REST_URL = "/inviteUser";

    @Override
    public RestResult<Void> saveUserInvite(InviteUserResource inviteUserResource) {
        String url = INVITE_REST_URL + "/saveInvite";
        return postWithRestResult(url, inviteUserResource, Void.class);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", INVITE_REST_URL, "checkExistingUser", inviteHash), Boolean.class);
    }

    @Override
    public RestResult<RoleInviteResource> getInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", INVITE_REST_URL, "getInvite", inviteHash), RoleInviteResource.class);
    }

    @Override
    public RestResult<RoleInvitePageResource> getPendingInternalUserInvites(int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(INVITE_REST_URL + "/internal/pending", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, RoleInvitePageResource.class);
    }

    @Override
    public RestResult<List<ExternalInviteResource>> findExternalInvites(String searchString, SearchCategory searchCategory) {
        return getWithRestResult(INVITE_REST_URL + "/findExternalInvites?searchString=" + searchString + "&searchCategory=" + searchCategory.name(), externalInviteResourceListType());
    }

    @Override
    public RestResult<Void> resendInternalUserInvite(long inviteId) {
        return putWithRestResult(INVITE_REST_URL + "/internal/pending/" + inviteId + "/resend", Void.class);
    }
}
