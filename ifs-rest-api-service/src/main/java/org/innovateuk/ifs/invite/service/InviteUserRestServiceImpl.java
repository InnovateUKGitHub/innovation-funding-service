package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.lang.String.format;

/**
 * A typical RestService to use as a client API on the web-service side for the data-service functionality.
 *
 * REST service for Invite User
 */
@Service
public class InviteUserRestServiceImpl extends BaseRestService implements InviteUserRestService {
    private static final String inviteRestUrl = "/inviteUser";

    @Override
    public RestResult<Void> saveUserInvite(InviteUserResource inviteUserResource) {
        String url = inviteRestUrl + "/saveInvite";
        return postWithRestResult(url, inviteUserResource, Void.class);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", inviteRestUrl, "checkExistingUser", inviteHash), Boolean.class);
    }

    @Override
    public RestResult<RoleInviteResource> getInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", inviteRestUrl, "getInvite", inviteHash), RoleInviteResource.class);
    }

    @Override
    public RestResult<RoleInvitePageResource> getPendingInternalUserInvites(int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(inviteRestUrl + "/internal/pending", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, RoleInvitePageResource.class);
    }
}
