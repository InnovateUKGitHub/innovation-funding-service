package com.worth.ifs.project;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsController {

	@Autowired
	private ApplicationService applicationService;
	
    @RequestMapping(value = "/{projectId}/startdate", method = RequestMethod.GET)
    public String projectDetails(Model model, @PathVariable("projectId") final Long projectId) {
    	
    	ApplicationResource project = applicationService.getById(projectId);
    	model.addAttribute("model", new ProjectDetailsStartDateViewModel(project));
        return "project/details-start-date";
    }
}
