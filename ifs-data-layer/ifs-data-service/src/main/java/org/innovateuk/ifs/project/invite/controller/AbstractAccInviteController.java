package org.innovateuk.ifs.project.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.transactional.AccInviteService;
import org.springframework.web.bind.annotation.*;

public abstract class AbstractAccInviteController {

    protected abstract AccInviteService getInviteService();

    @PostMapping
    public RestResult<Void> invitePartnerOrganisation(@PathVariable long projectId, @RequestBody SendProjectPartnerInviteResource inviteOrganisationResource) {
        return getInviteService().sendInvite(projectId, inviteOrganisationResource).toPostResponse();
    }

    @PostMapping("/{inviteId}/resend")
    public RestResult<Void> resendInvite(@PathVariable long inviteId) {
        return getInviteService().resendInvite(inviteId).toPostResponse();
    }

    @DeleteMapping("/{inviteId}")
    public RestResult<Void> deleteInvite(@PathVariable long inviteId) {
        return getInviteService().deleteInvite(inviteId).toDeleteResponse();
    }

    @GetMapping("/{hash}")
    public RestResult<SentProjectPartnerInviteResource> getInviteByHash(@PathVariable String hash) {
        return getInviteService().getInviteByHash(hash).toGetResponse();
    }

    @PostMapping("/{inviteId}/organisation/{organisationId}/accept")
    public RestResult<Void> acceptInvite(@PathVariable long inviteId, @PathVariable long organisationId) {
        return getInviteService().acceptInvite(inviteId, organisationId).toPostResponse();
    }
}
