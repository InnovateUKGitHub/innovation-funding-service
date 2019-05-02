package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to the project team.
 */

@Controller
@RequestMapping("/project")
public class ProjectTeamController {

    private ProjectTeamViewModelPopulator projectTeamPopulator;

    private ProjectRestService projectRestService;

    ProjectTeamController() {}

    @Autowired
    public ProjectTeamController(ProjectTeamViewModelPopulator projectTeamPopulator,
                                 ProjectRestService projectRestService) {
        this.projectTeamPopulator = projectTeamPopulator;
        this.projectRestService = projectRestService;
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @GetMapping("/{projectId}/team")
    public String viewProjectTeam(@PathVariable("projectId") final long projectId,
                                  Model model,
                                  UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser));
        return "project/project-team";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @PostMapping("/{projectId}/team/remove-user/{userId}")
    public String removeUser(@PathVariable("projectId") final long projectId,
                             @PathVariable("userId") final long userId) {
        projectRestService.removeUser(projectId, userId).getSuccess();
        return "redirect:/project/" + projectId + "/team";
    }

}
