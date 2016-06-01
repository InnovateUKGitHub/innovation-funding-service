package com.worth.ifs.project;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.ProjectService;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import com.worth.ifs.project.form.ProjectManagerForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.UserResource;

/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsController {

	@Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;
    
    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;
    
    @Autowired
    private UserAuthenticationService userAuthenticationService;
	
    @RequestMapping(value = "/{projectId}/details", method = RequestMethod.GET)
    public String projectDetail(Model model, @PathVariable("projectId") final Long projectId, HttpServletRequest request) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectId);
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        
        organisationDetailsModelPopulator.populateModel(model, projectId);
        
        model.addAttribute("project", projectResource);
        model.addAttribute("currentUser", user);
        model.addAttribute("currentOrganisation", user.getOrganisations().get(0));
        model.addAttribute("app", applicationResource);
        model.addAttribute("competition", competitionResource);
        return "project/detail";
    }
    
    @RequestMapping(value = "/{projectId}/details/project-manager", method = RequestMethod.GET)
    public String projectManager(Model model, @PathVariable("projectId") final Long projectId) {
    	ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectId);

        ProjectManagerForm form = new ProjectManagerForm();
        form.setProjectManager(projectResource.getProjectManager());
        
        model.addAttribute("project", projectResource);
        model.addAttribute("app", applicationResource);
        model.addAttribute("form", form);
    	
        return "project/project-manager";
    }
    
    @RequestMapping(value = "/{projectId}/details/project-manager", method = RequestMethod.POST)
    public String updateProjectManager(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute ProjectManagerForm form, BindingResult bindingResult) {
        
    	ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectId);
        
        //TODO check if the entered id is one of the people in the team
        
        if(bindingResult.hasErrors()) {
        	 model.addAttribute("project", projectResource);
             model.addAttribute("app", applicationResource);
             model.addAttribute("form", form);
             return "project/project-manager";
        }
        
        
    	//TODO save it.
    	
        return "redirect:/project/" + projectId + "/details";
    }
}
