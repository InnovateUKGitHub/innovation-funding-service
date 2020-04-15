package org.innovateuk.ifs.grants.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.grants.transactional.GrantsInviteService;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/{projectId}/grant-invite")
public class GrantsInviteController {

    @Autowired
    private GrantsInviteService grantsInviteService;

    @PostMapping
    public RestResult<Void> invitePartnerOrganisation(@PathVariable long projectId, @RequestBody GrantsInviteResource grantsInviteResource) {
        return grantsInviteService.sendInvite(projectId, grantsInviteResource).toPostResponse();
    }

    @PostMapping("/{inviteId}/resend")
    public RestResult<Void> resendInvite(@PathVariable long inviteId) {
        return grantsInviteService.resendInvite(inviteId).toPostResponse();
    }

    @GetMapping("/{hash}")
    public RestResult<SentGrantsInviteResource> getInviteByHash(@PathVariable String hash) {
        return grantsInviteService.getInviteByHash(hash).toGetResponse();
    }

    @PostMapping("/{inviteId}/organisation/{organisationId}/accept")
    public RestResult<Void> acceptInvite(@PathVariable long inviteId, @PathVariable long organisationId) {
        return grantsInviteService.acceptInvite(inviteId, organisationId).toPostResponse();
    }
}
