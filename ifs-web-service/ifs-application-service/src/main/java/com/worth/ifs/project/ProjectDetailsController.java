package com.worth.ifs.project;

import com.worth.ifs.application.service.ProjectService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.service.ProjectRestService;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsController {

	@Autowired
	private ProjectService projectService;

    @Autowired
    private ProjectRestService applicationRestService;

    @RequestMapping(value = "/{projectId}/startdate", method = RequestMethod.GET)
    public String viewStartDate(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute("form") ProjectDetailsStartDateViewModel.ProjectDetailsStartDateViewModelForm form) {
    	
    	ProjectResource project = projectService.getById(projectId);
    	model.addAttribute("model", new ProjectDetailsStartDateViewModel(project));
        LocalDate defaultStartDate = LocalDate.of(project.getTargetStartDate().getYear(), project.getTargetStartDate().getMonth(), 1);
        form.setProjectStartDate(defaultStartDate);
        return "project/details-start-date";
    }

    @RequestMapping(value = "/{projectId}/startdate", method = RequestMethod.POST)
    public String updateStartDate(@PathVariable("projectId") final Long projectId,
                                  @ModelAttribute("form") ProjectDetailsStartDateViewModel.ProjectDetailsStartDateViewModelForm form) {

        applicationRestService.updateProjectStartDate(projectId, form.getProjectStartDate());
        return "redirect:/project/" + projectId + "/startdate";
    }
}
