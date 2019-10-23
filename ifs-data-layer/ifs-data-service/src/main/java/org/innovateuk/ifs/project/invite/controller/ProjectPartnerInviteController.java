package org.innovateuk.ifs.project.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.transactional.ProjectPartnerInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/project/{projectId}/project-partner-invite")
public class ProjectPartnerInviteController {

    @Autowired
    private ProjectPartnerInviteService projectPartnerInviteService;

    @PostMapping
    public RestResult<Void> invitePartnerOrganisation(@PathVariable long projectId, @RequestBody ProjectPartnerInviteResource inviteOrganisationResource) {
        return projectPartnerInviteService.invitePartnerOrganisation(projectId, inviteOrganisationResource).toPostResponse();
    }
}
