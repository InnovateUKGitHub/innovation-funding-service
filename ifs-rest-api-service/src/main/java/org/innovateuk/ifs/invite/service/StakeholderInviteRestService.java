package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;

public interface StakeholderInviteRestService {
    RestResult<StakeholderInviteResource> getInvite(String inviteHash);
}
