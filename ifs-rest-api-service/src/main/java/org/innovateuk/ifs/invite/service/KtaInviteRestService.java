package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;

import java.util.List;

public interface KtaInviteRestService {

    RestResult<Void> saveKtaInvite(ApplicationKtaInviteResource inviteResource);
    RestResult<List<ApplicationKtaInviteResource>> getKtaInvitesByApplication(Long applicationId);
    RestResult<Void> removeKtaInvite(long inviteId);
    RestResult<Void> resendKtaInvite(ApplicationKtaInviteResource invite);
}
