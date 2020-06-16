package org.innovateuk.ifs.grants.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
public class GrantsInviteRestServiceImpl extends BaseRestService implements GrantsInviteRestService {
    private static final String BASE_URL = "/project/%d/grant-invite";

    @Override
    public RestResult<List<SentGrantsInviteResource>> getAllForProject(long projectId) {
        return getWithRestResult(format(BASE_URL, projectId), new ParameterizedTypeReference<List<SentGrantsInviteResource>>() {});
    }

    @Override
    public RestResult<Void> invite(long projectId, GrantsInviteResource grantsInviteResource) {
        return postWithRestResult(format(BASE_URL, projectId), grantsInviteResource, Void.class);
    }

    @Override
    public RestResult<Void> resendInvite(long projectId, long inviteId) {
        return postWithRestResult(format(BASE_URL + "/%d/resend", projectId, inviteId), Void.class);
    }

    @Override
    public RestResult<SentGrantsInviteResource> getInviteByHash(long projectId, String hash) {
        return getWithRestResultAnonymous(format(BASE_URL + "/%s", projectId, hash), SentGrantsInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(long projectId, long inviteId) {
        return postWithRestResultAnonymous(format(BASE_URL + "/%d/accept", projectId, inviteId), Void.class);
    }
}
