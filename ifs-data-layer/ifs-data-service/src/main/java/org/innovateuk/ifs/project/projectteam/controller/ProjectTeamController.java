package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.project.projectteam.transactional.ProjectTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * ProjectController exposes Project Details data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectTeamController {

    @Autowired
    private ProjectTeamService projectTeamService;

    @PostMapping("/{projectId}/team/invite")
    public RestResult<Void> inviteTeamMember(@PathVariable("projectId") final Long projectId,
                                             @RequestBody @Valid final ProjectUserInviteResource inviteResource) {
        return projectTeamService.inviteTeamMember(projectId, inviteResource).toPostResponse();
    }
}
