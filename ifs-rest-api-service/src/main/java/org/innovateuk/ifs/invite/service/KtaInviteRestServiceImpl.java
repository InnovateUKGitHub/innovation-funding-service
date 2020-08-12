package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.springframework.stereotype.Service;

@Service
public class KtaInviteRestServiceImpl extends BaseRestService implements KtaInviteRestService {

    private final static String KTA_INVITE_REST_URL = "/kta-invite";

    @Override
    public RestResult<Void> saveKtaInvite(ApplicationKtaInviteResource inviteResource) {
        String url = KTA_INVITE_REST_URL + "/save-kta-invite";
        return postWithRestResult(url, inviteResource, Void.class);
    }

    @Override
    public RestResult<ApplicationKtaInviteResource> getKtaInviteByApplication(long applicationId) {
        String url = KTA_INVITE_REST_URL + "/get-kta-invite-by-application-id/"+ applicationId;
        return getWithRestResult(url, ApplicationKtaInviteResource.class);
    }

    @Override
    public RestResult<Void> removeKtaInviteByApplication(long applicationId) {
        String url = KTA_INVITE_REST_URL + String.format("/remove-kta-invite-by-application/%s", applicationId);
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<Void> resendKtaInvite(ApplicationKtaInviteResource inviteResource) {
        String url = KTA_INVITE_REST_URL + "/resend-kta-invite";
        return postWithRestResult(url, inviteResource, Void.class);
    }
}
