package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.transactional.ApplicationKtaInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kta-invite")
public class ApplicationKtaInviteController {

    @Autowired
    private ApplicationKtaInviteService applicationKtaInviteService;

    @GetMapping("/get-kta-invite-by-application-id/{applicationId}")
    public RestResult<ApplicationKtaInviteResource> getKtaInviteByApplication(@PathVariable long applicationId) {
        return applicationKtaInviteService.getKtaInviteByApplication(applicationId).toGetResponse();
    }

    @PostMapping("/save-kta-invite")
    public RestResult<Void> saveKtaInvite(@RequestBody ApplicationKtaInviteResource inviteResource) {
        return applicationKtaInviteService.saveKtaInvite(inviteResource).toPostCreateResponse();
    }

    @PostMapping("/resend-kta-invite")
    public RestResult<Void> resendKtaInvite(@RequestBody ApplicationKtaInviteResource inviteResource) {
        return applicationKtaInviteService.resendKtaInvite(inviteResource).toPostCreateResponse();
    }

    @DeleteMapping("/remove-kta-invite-by-application/{applicationId}")
    public RestResult<Void> removeKtaInvite(@PathVariable("applicationId") long applicationId) {
        return applicationKtaInviteService.removeKtaInviteByApplication(applicationId).toDeleteResponse();
    }

    @GetMapping("/hash/{hash}")
    public RestResult<ApplicationKtaInviteResource> getKtaInviteByHash(@PathVariable String hash) {
        return applicationKtaInviteService.getKtaInviteByHash(hash).toGetResponse();
    }

    @PostMapping("/hash/{hash}")
    public RestResult<Void> acceptInvite(@PathVariable String hash) {
        return applicationKtaInviteService.acceptInvite(hash).toGetResponse();
    }
}
