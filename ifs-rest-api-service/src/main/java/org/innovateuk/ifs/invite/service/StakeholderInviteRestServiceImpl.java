package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;

import static java.lang.String.format;

public class StakeholderInviteRestServiceImpl extends BaseRestService implements StakeholderInviteRestService  {

    private static final String INVITE_REST_URL = "/inviteStakeholder";
    @Override
    public RestResult<StakeholderInviteResource> getInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", INVITE_REST_URL, "getInvite", inviteHash), RoleInviteResource.class);
    }
}
