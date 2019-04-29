package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to the project manager.
 */
@Controller
@RequestMapping("/project")
public class ProjectManagerController {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_DETAILS_SECTION')")
    @GetMapping("/{projectId}/team/project-manager")
    public String viewProjectTeam(@PathVariable("projectId") final long projectId,
                                  Model model,
                                  UserResource loggedInUser) {
        return "project/team/project-manager";
    }
}
