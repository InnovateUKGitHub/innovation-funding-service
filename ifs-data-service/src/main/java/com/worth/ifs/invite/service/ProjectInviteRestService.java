package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.user.resource.UserResource;

public interface ProjectInviteRestService {
    RestResult<Boolean> checkExistingUser(String inviteHash);
    RestResult<UserResource> getUser(String inviteHash);
    RestResult<ApplicationInviteResource> getInviteByHash(String hash);
}
