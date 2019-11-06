package org.innovateuk.ifs.project.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.transactional.ProjectPartnerInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/project/{projectId}/project-partner-invite")
public class ProjectPartnerInviteController {

    @Autowired
    private ProjectPartnerInviteService projectPartnerInviteService;

    @PostMapping
    public RestResult<Void> invitePartnerOrganisation(@PathVariable long projectId, @RequestBody SendProjectPartnerInviteResource inviteOrganisationResource) {
        return projectPartnerInviteService.invitePartnerOrganisation(projectId, inviteOrganisationResource).toPostResponse();
    }

    @GetMapping
    public RestResult<List<SentProjectPartnerInviteResource>> getPartnerInvites(@PathVariable long projectId) {
        return projectPartnerInviteService.getPartnerInvites(projectId).toGetResponse();
    }

    @PostMapping("/{inviteId}/resend")
    public RestResult<Void> resendInvite(@PathVariable long inviteId) {
        return projectPartnerInviteService.resendInvite(inviteId).toPostResponse();
    }

    @DeleteMapping("/{inviteId}")
    public RestResult<Void> deleteInvite(@PathVariable long inviteId) {
        return projectPartnerInviteService.deleteInvite(inviteId).toDeleteResponse();
    }

    @GetMapping("/{hash}")
    public RestResult<SentProjectPartnerInviteResource> getInviteByHash(@PathVariable String hash) {
        return projectPartnerInviteService.getInviteByHash(hash).toGetResponse();
    }

    @PostMapping("/{inviteId}/organisation/{organisationId}/accept")
    public RestResult<Void> acceptInvite(@PathVariable long inviteId, @PathVariable long organisationId) {
        return projectPartnerInviteService.acceptInvite(inviteId, organisationId).toPostResponse();
    }
}
