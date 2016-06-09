package com.worth.ifs.project;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.form.FinanceContactForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.service.ProjectRestService;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateForm;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import com.worth.ifs.project.viewmodel.ProjectDetailsViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.controller.RestFailuresToValidationErrorBindingUtils.bindAnyErrorsToField;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;

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
    private OrganisationRestService organisationRestService;
    
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    
    @Autowired
    private UserService userService;
	
    @Autowired
    private ProjectRestService projectRestService;
    
    @Autowired
    private ProcessRoleService processRoleService;

    @RequestMapping(value = "/{projectId}/details", method = RequestMethod.GET)
    public String projectDetail(Model model, @PathVariable("projectId") final Long projectId, HttpServletRequest request) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectId);
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        List<OrganisationResource> partnerOrganisations = getPartnerOrganisations(projectUsers);

        model.addAttribute("model", new ProjectDetailsViewModel(projectResource, user, user.getOrganisations().get(0), partnerOrganisations, applicationResource, projectUsers, competitionResource));
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
    
    @RequestMapping(value = "/{projectId}/details/finance-contact", method = RequestMethod.GET)
    public String viewFinanceContact(Model model, @PathVariable("projectId") final Long projectId, @RequestParam(value="organisation",required=false) Long organisation, HttpServletRequest request) {
        if(organisation == null) {
    		return redirectToProjectDetails(projectId);
        }
        
    	if(!userIsInOrganisation(organisation, request)){
    		return redirectToProjectDetails(projectId);
    	}
    	
    	if(!anyUsersInGivenOrganisationForProject(projectId, organisation)){
    		return redirectToProjectDetails(projectId);
    	}
		
        return modelForFinanceContact(model, projectId, organisation, request);
    }
    
	@RequestMapping(value = "/{projectId}/details/finance-contact", method = RequestMethod.POST)
    public String updateFinanceContact(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute FinanceContactForm form, BindingResult bindingResult, HttpServletRequest request) {
        
		if(!userIsInOrganisation(form.getOrganisation(), request)){
    		return redirectToProjectDetails(projectId);
    	}
    	
    	if(!anyUsersInGivenOrganisationForProject(projectId, form.getOrganisation())){
    		return redirectToProjectDetails(projectId);
    	}

        if(bindingResult.hasErrors()) {
        	return modelForFinanceContact(model, projectId, request, form);
        }
        
        if(!userIsInOrganisationForProject(projectId, form.getOrganisation(), form.getFinanceContact())) {
        	return modelForFinanceContact(model, projectId, request, form);
        }
        
        projectService.updateFinanceContact(projectId, form.getOrganisation(), form.getFinanceContact());
    	
        return redirectToProjectDetails(projectId);
    }

	private String modelForFinanceContact(Model model, Long projectId, Long organisation, HttpServletRequest request) {

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        List<ProjectUserResource> financeContacts = simpleFilter(projectUsers, pr -> pr.isFinanceContact() && organisation.equals(pr.getOrganisation()));

		FinanceContactForm form = new FinanceContactForm();
		form.setOrganisation(organisation);

        if (!financeContacts.isEmpty()) {
            form.setFinanceContact(getOnlyElement(financeContacts).getUser());
        }

		return modelForFinanceContact(model, projectId, request, form);
	}

	private String modelForFinanceContact(Model model, Long projectId, HttpServletRequest request, FinanceContactForm form) {
		ApplicationResource applicationResource = applicationService.getById(projectId);
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
		List<ProcessRoleResource> thisOrganisationUsers = userService.getOrganisationProcessRoles(applicationResource, form.getOrganisation());
		ProjectResource projectResource = projectService.getById(projectId);
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());
        
        model.addAttribute("organisationUsers", thisOrganisationUsers);
        model.addAttribute("form", form);
        model.addAttribute("project", projectResource);
        model.addAttribute("currentUser", user);
        model.addAttribute("currentOrganisation", user.getOrganisations().get(0));
        model.addAttribute("app", applicationResource);
        model.addAttribute("competition", competitionResource);
        return "project/finance-contact";
	}
    
    private boolean userIsInOrganisation(Long organisation, HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        
        return organisation.equals(user.getOrganisations().get(0));
    }
    
    private boolean anyUsersInGivenOrganisationForProject(Long projectId, Long organisation) {
        ApplicationResource applicationResource = applicationService.getById(projectId);
		List<ProcessRoleResource> thisOrganisationUsers = userService.getOrganisationProcessRoles(applicationResource, organisation);
		
		return !thisOrganisationUsers.isEmpty();
	}
    
	private boolean userIsInOrganisationForProject(Long projectId, Long organisation, Long userId) {
		if(userId == null) {
			return false;
		}
		ProcessRoleResource processRoleForUserOnApplication = processRoleService.findProcessRole(userId, projectId);
		if(processRoleForUserOnApplication == null) {
			return false;
		}
		return organisation.equals(processRoleForUserOnApplication.getOrganisation());
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

    private List<OrganisationResource> getPartnerOrganisations(final List<ProjectUserResource> projectRoles) {

        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);

        final Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        SortedSet<OrganisationResource> organisationSet = projectRoles.stream()
                .filter(uar -> uar.getRoleName().equals(PARTNER.getName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
                .collect(Collectors.toCollection(supplier));

        return new ArrayList<>(organisationSet);
    }
}
