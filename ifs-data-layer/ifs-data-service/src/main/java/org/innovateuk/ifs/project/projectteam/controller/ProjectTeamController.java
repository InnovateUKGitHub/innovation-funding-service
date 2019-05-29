package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.project.projectteam.transactional.ProjectTeamService;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * ProjectController exposes Project Team data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectTeamController {

    private ProjectTeamService projectTeamService;

    public ProjectTeamController(ProjectTeamService projectTeamService) {
        this.projectTeamService = projectTeamService;
    }

    @PostMapping("/{projectId}/team/remove-user/{userId}")
    public RestResult<Void> removeUser(@PathVariable("projectId") final long projectId,
                                       @PathVariable("userId") final long userId) {
        ProjectUserCompositeId composite = new ProjectUserCompositeId(projectId, userId);
        return projectTeamService.removeUser(composite).toPostResponse();
    }

    @PostMapping("/{projectId}/team/remove-invite/{inviteId}")
    public RestResult<Void> removeInvite(@PathVariable("projectId") final long projectId,
                                       @PathVariable("inviteId") final long inviteId) {
        return projectTeamService.removeInvite(inviteId, projectId).toPostResponse();
    }

    @PostMapping("/{projectId}/team/invite")
    public RestResult<Void> inviteTeamMember(@PathVariable("projectId") final long projectId,
                                             @RequestBody @Valid final ProjectUserInviteResource inviteResource) {
        return projectTeamService.inviteTeamMember(projectId, inviteResource).toPostResponse();
    }
}
