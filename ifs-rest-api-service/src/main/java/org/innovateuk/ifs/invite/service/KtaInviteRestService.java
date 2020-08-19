package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;

public interface KtaInviteRestService {

    RestResult<Void> saveKtaInvite(ApplicationKtaInviteResource inviteResource);
    RestResult<ApplicationKtaInviteResource> getKtaInviteByApplication(long applicationId);
    RestResult<Void> removeKtaInviteByApplication(long applicationId);
    RestResult<Void> resendKtaInvite(ApplicationKtaInviteResource invite);
}
