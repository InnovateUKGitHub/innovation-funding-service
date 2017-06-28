package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.springframework.stereotype.Service;

/**
 * A typical RestService to use as a client API on the web-service side for the data-service functionality.
 *
 * REST service for Invite User
 */
@Service
public class InviteUserRestServiceImpl extends BaseRestService implements InviteUserRestService {

    @Override
    public RestResult<Void> saveUserInvite(InviteUserResource inviteUserResource) {
        String url = "/inviteUser" + "/saveInvite";
        return postWithRestResult(url, inviteUserResource, Void.class);
    }
}
