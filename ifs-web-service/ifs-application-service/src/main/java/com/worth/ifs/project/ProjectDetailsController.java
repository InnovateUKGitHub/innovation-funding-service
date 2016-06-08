package com.worth.ifs.project;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.ProjectService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.service.ProjectRestService;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateForm;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

import static com.worth.ifs.controller.RestFailuresToValidationErrorBindingUtils.bindAnyErrorsToField;

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
    
    @Autowired
    private UserService userService;
	
    @Autowired
    private ProjectRestService projectRestService;

    @RequestMapping(value = "/{projectId}/details", method = RequestMethod.GET)
    public String projectDetail(Model model, @PathVariable("projectId") final Long projectId, HttpServletRequest request) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectId);
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        Boolean userIsLeadApplicant = userService.isLeadApplicant(user.getId(), applicationResource);
        
        organisationDetailsModelPopulator.populateModel(model, projectId);
        
        model.addAttribute("project", projectResource);
        model.addAttribute("currentUser", user);
        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        
        model.addAttribute("currentOrganisation", user.getOrganisations().get(0));
        model.addAttribute("app", applicationResource);
        model.addAttribute("competition", competitionResource);
        return "project/detail";
    }

    @RequestMapping(value = "/{projectId}/details/start-date", method = RequestMethod.GET)
    public String viewStartDate(Model model, @PathVariable("projectId") final Long projectId,
                                @ModelAttribute("form") ProjectDetailsStartDateForm form) {
    	
    	ProjectResource project = projectService.getById(projectId);
    	model.addAttribute("model", new ProjectDetailsStartDateViewModel(project));
        LocalDate defaultStartDate = project.getTargetStartDate().withDayOfMonth(1);
        form.setProjectStartDate(defaultStartDate);
        return "project/details-start-date";
    }

    @RequestMapping(value = "/{projectId}/details/start-date", method = RequestMethod.POST)
    public String updateStartDate(@PathVariable("projectId") final Long projectId,
                                  @ModelAttribute("form") ProjectDetailsStartDateForm form,
                                  Model model,
                                  BindingResult bindingResult) {

        RestResult<Void> updateResult = projectRestService.updateProjectStartDate(projectId, form.getProjectStartDate());
        return handleErrorsOrRedirectToProjectOverview("projectStartDate", projectId, model, form, bindingResult, updateResult);
    }

    private String handleErrorsOrRedirectToProjectOverview(
            String fieldName, long projectId, Model model,
            ProjectDetailsStartDateForm form, BindingResult bindingResult,
            RestResult<?> result) {

        if (result.isFailure()) {
            bindAnyErrorsToField(result, fieldName, bindingResult, form);
            model.addAttribute("form", form);
            return viewStartDate(model, projectId, form);
        }

        return redirectToProjectDetails(projectId);
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }
}
