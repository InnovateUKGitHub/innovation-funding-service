package com.worth.ifs.project;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.ProjectService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import com.worth.ifs.project.form.ProjectManagerForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.service.ProjectRestService;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.ProcessRoleService;
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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.worth.ifs.controller.RestFailuresToValidationErrorBindingUtils.bindAnyErrorsToField;
import static java.util.Arrays.asList;

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
    private ProcessRoleService processRoleService;
    
    @Autowired
    private ProjectRestService projectRestService;

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
        model.addAttribute("projectManager", getProjectManagerProcessRole(projectResource.getId()));
        return "project/detail";
    }
    
    @RequestMapping(value = "/{projectId}/details/project-manager", method = RequestMethod.GET)
    public String viewProjectManager(Model model, @PathVariable("projectId") final Long projectId, HttpServletRequest request) throws InterruptedException, ExecutionException {
		if(!userIsLeadApplicant(projectId, request)) {
			return redirectToProjectDetails(projectId);
		}
    	ProjectManagerForm form = populateOriginalProjectManagerForm(projectId);
        
        ApplicationResource applicationResource = applicationService.getById(projectId);

        populateProjectManagerModel(model, projectId, form, applicationResource);
    	
        return "project/project-manager";
    }

    @RequestMapping(value = "/{projectId}/details/project-manager", method = RequestMethod.POST)
    public String updateProjectManager(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute ProjectManagerForm form, BindingResult bindingResult, HttpServletRequest request) {
    	if(!userIsLeadApplicant(projectId, request)) {
			return redirectToProjectDetails(projectId);
		}
        ApplicationResource applicationResource = applicationService.getById(projectId);
        
        if(bindingResult.hasErrors()) {
        	populateProjectManagerModel(model, projectId, form, applicationResource);
            return "project/project-manager";
        }
        
        if(!userIsInLeadPartnerOrganisation(applicationResource, form.getProjectManager())) {
        	populateProjectManagerModel(model, projectId, form, applicationResource);
            return "project/project-manager";
        }
        
        projectService.updateProjectManager(projectId, form.getProjectManager());
    	
        return redirectToProjectDetails(projectId);
    }

    private boolean userIsLeadApplicant(Long projectId, HttpServletRequest request) {
    	UserResource user = userAuthenticationService.getAuthenticatedUser(request);
    	ApplicationResource applicationResource = applicationService.getById(projectId);
    	
    	return userService.isLeadApplicant(user.getId(), applicationResource);
    }
    
	private ProjectManagerForm populateOriginalProjectManagerForm(final Long projectId) throws InterruptedException, ExecutionException {

        Future<ProcessRoleResource> processRoleResource = getProjectManagerProcessRole(projectId);
    	
        ProjectManagerForm form = new ProjectManagerForm();
        if(processRoleResource != null) {
			form.setProjectManager(processRoleResource.get().getUser());
        }
		return form;
	}

    private Future<ProcessRoleResource> getProjectManagerProcessRole(Long projectId) {
        ProjectResource projectResource = projectService.getById(projectId);
        Future<ProcessRoleResource> processRoleResource;
        if(projectResource.getProjectManager() != null) {
            processRoleResource = processRoleService.getById(projectResource.getProjectManager());
        } else {
            processRoleResource = null;
        }
        return processRoleResource;
    }

    private void populateProjectManagerModel(Model model, final Long projectId, ProjectManagerForm form,
			ApplicationResource applicationResource) {
		ProjectResource projectResource = projectService.getById(projectId);
		
		ProcessRoleResource lead = userService.getLeadApplicantProcessRoleOrNull(applicationResource);
		List<ProcessRoleResource> leadPartnerUsers = userService.getLeadPartnerOrganisationProcessRoles(applicationResource);
		List<ProcessRoleResource> leadPartnerUsersExcludingLead = leadPartnerUsers.stream()
				.filter(prr -> !lead.getUser().equals(prr.getUser()))
				.collect(Collectors.toList());
		
		List<ProcessRoleResource> allUsers = Stream.of(asList(lead), leadPartnerUsersExcludingLead)
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());

		model.addAttribute("allUsers", allUsers);
		model.addAttribute("project", projectResource);
		model.addAttribute("app", applicationResource);
		model.addAttribute("form", form);
	}

	private boolean userIsInLeadPartnerOrganisation(ApplicationResource applicationResource, Long projectManager) {
		
		if(projectManager == null) {
			return false;
		}
        List<ProcessRoleResource> leadPartnerUsers = userService.getLeadPartnerOrganisationProcessRoles(applicationResource);

        return leadPartnerUsers.stream().anyMatch(prr -> projectManager.equals(prr.getUser()));
	}

    @RequestMapping(value = "/{projectId}/details/start-date", method = RequestMethod.GET)
    public String viewStartDate(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute("form") ProjectDetailsStartDateViewModel.ProjectDetailsStartDateViewModelForm form, HttpServletRequest request) {
    	if(!userIsLeadApplicant(projectId, request)) {
			return redirectToProjectDetails(projectId);
		}
    	ProjectResource project = projectService.getById(projectId);
    	model.addAttribute("model", new ProjectDetailsStartDateViewModel(project));
        LocalDate defaultStartDate = LocalDate.of(project.getTargetStartDate().getYear(), project.getTargetStartDate().getMonth(), 1);
        form.setProjectStartDate(defaultStartDate);
        return "project/details-start-date";
    }

    @RequestMapping(value = "/{projectId}/details/start-date", method = RequestMethod.POST)
    public String updateStartDate(@PathVariable("projectId") final Long projectId,
                                  @ModelAttribute("form") ProjectDetailsStartDateViewModel.ProjectDetailsStartDateViewModelForm form,
                                  Model model,
                                  BindingResult bindingResult, HttpServletRequest request) {
    	if(!userIsLeadApplicant(projectId, request)) {
			return redirectToProjectDetails(projectId);
		}
        RestResult<Void> updateResult = projectRestService.updateProjectStartDate(projectId, form.getProjectStartDate());
        return handleErrorsOrRedirectToProjectOverview("projectStartDate", projectId, model, form, bindingResult, updateResult, request);
    }

    private String handleErrorsOrRedirectToProjectOverview(
            String fieldName, Long projectId, Model model,
            ProjectDetailsStartDateViewModel.ProjectDetailsStartDateViewModelForm form, BindingResult bindingResult,
            RestResult<?> result, HttpServletRequest request) {

        if (result.isFailure()) {
            bindAnyErrorsToField(result, fieldName, bindingResult, form);
            model.addAttribute("form", form);
            return viewStartDate(model, projectId, form, request);
        }

        return redirectToProjectDetails(projectId);
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }
}
