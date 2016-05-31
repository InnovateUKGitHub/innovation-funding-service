package com.worth.ifs.project;

import com.worth.ifs.application.LocalDatePropertyEditor;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsController {

	@Autowired
	private ApplicationService applicationService;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder, WebRequest webRequest) {
        dataBinder.registerCustomEditor(LocalDate.class, new LocalDatePropertyEditor(webRequest));
    }

    @RequestMapping(value = "/{projectId}/startdate", method = RequestMethod.GET)
    public String viewStartDate(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute("form") ProjectDetailsStartDateViewModel.ProjectDetailsStartDateViewModelForm form) {
    	
    	ApplicationResource project = applicationService.getById(projectId);
    	model.addAttribute("model", new ProjectDetailsStartDateViewModel(project));
        LocalDate defaultStartDate = LocalDate.of(project.getStartDate().getYear(), project.getStartDate().getMonth(), 1);
        form.setProjectStartDate(defaultStartDate);
        return "project/details-start-date";
    }

    @RequestMapping(value = "/{projectId}/startdate", method = RequestMethod.POST)
    public String updateStartDate(@PathVariable("projectId") final Long projectId,
                                  @ModelAttribute("form") ProjectDetailsStartDateViewModel.ProjectDetailsStartDateViewModelForm form) {

        System.out.println(">>>>>>>>>>>>>" + form.getProjectStartDate().getYear());
        return "redirect:/project/" + projectId + "/startdate";
    }
}
