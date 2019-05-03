package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.projectteam.transactional.ProjectTeamService;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProjectTeamController exposes Project Team data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectTeamController {

    private ProjectTeamService projectTeamService;

    public ProjectTeamController(ProjectTeamService projectTeamService) {
        this.projectTeamService = projectTeamService;
    }

    @PostMapping("/{projectId}/remove-user/{userId}")
    public RestResult<Void> removeUser(@PathVariable("projectId") final long projectId,
                                       @PathVariable("userId") final long userId) {
        ProjectUserCompositeId composite = new ProjectUserCompositeId(projectId, userId);
        return projectTeamService.removeUser(composite).toPostResponse();
    }
}
