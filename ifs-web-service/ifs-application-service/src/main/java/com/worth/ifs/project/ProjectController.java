package com.worth.ifs.project;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	private ApplicationService applicationService;
	
    @RequestMapping("/{projectId}")
    public String projectDetails(Model model, @PathVariable("projectId") final Long projectId, HttpServletRequest request) {
    	
    	ApplicationResource application = applicationService.getById(projectId);
    	model.addAttribute("project", application);
    	
        return "project/details";
    }
}
