package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;

public interface KtaInviteRestService {

    RestResult<Void> saveKtaInvite(ApplicationKtaInviteResource inviteResource);
    RestResult<ApplicationKtaInviteResource> getKtaInviteByApplication(Long applicationId);
    RestResult<Void> removeKtaInvite(long inviteId);
    RestResult<Void> resendKtaInvite(ApplicationKtaInviteResource invite);
}
