package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import static com.worth.ifs.invite.controller.InviteProjectController.*;

/**
 * A typical RestService to use as a client API on the web-service side for the data-service functionality .
 */
@Service
public class ProjectInviteRestServiceImpl extends BaseRestService implements ProjectInviteRestService {

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        String url = PROJECT_INVITE_BASE_URL + CHECK_EXISTING_USER_URL + inviteHash;
        return getWithRestResultAnonymous(url, Boolean.class);
    }

    @Override
    public RestResult<UserResource> getUser(String inviteHash) {
        String url = PROJECT_INVITE_BASE_URL + GET_USER_BY_HASH_MAPPING + inviteHash;
        return getWithRestResultAnonymous(url, UserResource.class);
    }

    @Override
    public RestResult<ApplicationInviteResource> getInviteByHash(String hash) {
        String url = PROJECT_INVITE_BASE_URL + GET_INVITE_BY_HASH + hash;
        return getWithRestResultAnonymous(url, ApplicationInviteResource.class);
    }
}
