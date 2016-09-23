package com.worth.ifs.project;

import com.worth.ifs.project.consortiumoverview.viewmodel.ProjectConsortiumStatusViewModel;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller will handle display of project activity status for all partners
 */
@Controller
@RequestMapping("/project/{projectId}/team-status")
public class ProjectTeamStatusController {

    @Autowired
    private ProjectService projectService;

    @RequestMapping(method = GET)
    public String viewProjectTeamStatus(Model model, @PathVariable("projectId") final Long projectId) {
        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId, Optional.empty());
        model.addAttribute("model", new ProjectConsortiumStatusViewModel(projectId, teamStatus));
        return "project/consortium-status";
    }
}
