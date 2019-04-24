package org.innovateuk.ifs.project.projectteam.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to project details.
 */

@Controller
@RequestMapping("/project")
public class ProjectTeamController {

    // TODO: security
    @GetMapping("/{projectId}/team")
    public String viewProjectTeam(@PathVariable("projectId") final Long projectId) {
        return "project/project-team";
    }

}
