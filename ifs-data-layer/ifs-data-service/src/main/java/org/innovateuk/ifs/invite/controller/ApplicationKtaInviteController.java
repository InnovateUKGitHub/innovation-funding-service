package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.transactional.ApplicationKtaInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kta-invite")
public class ApplicationKtaInviteController {

    @Autowired
    private ApplicationKtaInviteService applicationKtaInviteService;

    @GetMapping("/get-kta-invites-by-application-id/{applicationId}")
    public RestResult<List<ApplicationKtaInviteResource>> getKtaInvitesByApplication(@PathVariable("applicationId") Long applicationId) {
        return applicationKtaInviteService.getKtaInvitesByApplication(applicationId).toGetResponse();
    }

    @PostMapping("/save-kta-invite")
    public RestResult<Void> saveKtaInvite(@RequestBody ApplicationKtaInviteResource inviteResource) {
        return applicationKtaInviteService.saveKtaInvite(inviteResource).toPostCreateResponse();
    }

    @PostMapping("/resend-kta-invite")
    public RestResult<Void> resendKtaInvite(@RequestBody ApplicationKtaInviteResource inviteResource) {
        return applicationKtaInviteService.resendKtaInvite(inviteResource).toPostCreateResponse();
    }

    @DeleteMapping("/remove-kta-invite/{inviteId}")
    public RestResult<Void> removeKtaInvite(@PathVariable("inviteId") long ktaInviteResourceId) {
        return applicationKtaInviteService.removeKtaApplicationInvite(ktaInviteResourceId).toDeleteResponse();
    }
}
