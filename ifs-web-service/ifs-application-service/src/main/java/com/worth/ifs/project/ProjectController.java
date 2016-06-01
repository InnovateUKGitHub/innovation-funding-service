package com.worth.ifs.project;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.ProjectService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;
	
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public String projectOverview(Model model, @PathVariable("projectId") final Long projectId) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectId);
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());
        model.addAttribute("project", projectResource);
        model.addAttribute("app", applicationResource);
        model.addAttribute("competition", competitionResource);
        return "project/overview";
    }
    
}
