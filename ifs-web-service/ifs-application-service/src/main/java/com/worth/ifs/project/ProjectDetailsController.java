package com.worth.ifs.project;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.application.form.AddressForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.BindingResultTarget;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.organisation.service.OrganisationAddressRestService;
import com.worth.ifs.project.form.FinanceContactForm;
import com.worth.ifs.project.form.ProjectManagerForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.viewmodel.*;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.worth.ifs.address.resource.OrganisationAddressType.*;
import static com.worth.ifs.controller.RestFailuresToValidationErrorBindingUtils.bindAnyErrorsToField;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static java.util.Arrays.asList;

/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsController {
    static final String FORM_ATTR_NAME = "form";
    private static final String MANUAL_ADDRESS = "manual-address";
    private static final String SEARCH_ADDRESS = "search-address";
    private static final String SELECT_ADDRESS = "select-address";

	@Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private CompetitionService competitionService;
    
    @Autowired
    private OrganisationRestService organisationRestService;
    
    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private AddressRestService addressRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @RequestMapping(value = "/{projectId}/details", method = RequestMethod.GET)
    public String projectDetail(Model model, @PathVariable("projectId") final Long projectId, HttpServletRequest request) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        Boolean userIsLeadApplicant = userService.isLeadApplicant(user.getId(), applicationResource);

	    List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        List<OrganisationResource> partnerOrganisations = getPartnerOrganisations(projectUsers);
        
        model.addAttribute("project", projectResource);
        model.addAttribute("currentUser", user);
        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("projectManager", getProjectManagerProcessRole(projectResource.getId()));
        model.addAttribute("model", new ProjectDetailsViewModel(projectResource, user, user.getOrganisations().get(0), partnerOrganisations, applicationResource, projectUsers, competitionResource));
        return "project/detail";
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
    
    private boolean anyUsersInGivenOrganisationForProject(Long projectId, Long organisationId) {
        List<ProjectUserResource> thisProjectUsers = projectService.getProjectUsersForProject(projectId);
        List<ProjectUserResource> projectUsersForOrganisation = simpleFilter(thisProjectUsers, user -> user.getOrganisation().equals(organisationId));
        return !projectUsersForOrganisation.isEmpty();
	}
    
	private boolean userIsInOrganisationForProject(Long projectId, Long organisationId, Long userId) {
		if(userId == null) {
			return false;
		}

        List<ProjectUserResource> thisProjectUsers = projectService.getProjectUsersForProject(projectId);
        List<ProjectUserResource> projectUsersForOrganisation = simpleFilter(thisProjectUsers, user -> user.getOrganisation().equals(organisationId));
        List<ProjectUserResource> projectUsersForUserAndOrganisation = simpleFilter(projectUsersForOrganisation, user -> user.getUser().equals(userId));

		return !projectUsersForUserAndOrganisation.isEmpty();
	}
    
    @RequestMapping(value = "/{projectId}/details/project-manager", method = RequestMethod.GET)
    public String viewProjectManager(Model model, @PathVariable("projectId") final Long projectId, HttpServletRequest request) throws InterruptedException, ExecutionException {
        ProjectResource projectResource = projectService.getById(projectId);

        if(!userIsLeadApplicant(projectResource.getApplication(), request)) {
			return redirectToProjectDetails(projectId);
		}
    	ProjectManagerForm form = populateOriginalProjectManagerForm(projectId);
        
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());

        populateProjectManagerModel(model, projectId, form, applicationResource);
    	
        return "project/project-manager";
    }

    @RequestMapping(value = "/{projectId}/details/project-manager", method = RequestMethod.POST)
    public String updateProjectManager(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute ProjectManagerForm form, BindingResult bindingResult, HttpServletRequest request) {
        ProjectResource projectResource = projectService.getById(projectId);

        if(!userIsLeadApplicant(projectResource.getApplication(), request)) {
			return redirectToProjectDetails(projectId);
		}
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        
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

    private boolean userIsLeadApplicant(Long applicationId, HttpServletRequest request) {
    	UserResource user = userAuthenticationService.getAuthenticatedUser(request);
    	ApplicationResource applicationResource = applicationService.getById(applicationId);
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
    public String viewStartDate(Model model, @PathVariable("projectId") final Long projectId,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsStartDateForm form,
                                HttpServletRequest request) {
        ProjectResource projectResource = projectService.getById(projectId);

        if(!userIsLeadApplicant(projectResource.getApplication(), request)) {
            return redirectToProjectDetails(projectId);
        }
        model.addAttribute("model", new ProjectDetailsStartDateViewModel(projectResource));
        LocalDate defaultStartDate = projectResource.getTargetStartDate().withDayOfMonth(1);
        form.setProjectStartDate(defaultStartDate);
        return "project/details-start-date";
    }

    @RequestMapping(value = "/{projectId}/details/start-date", method = RequestMethod.POST)
    public String updateStartDate(@PathVariable("projectId") final Long projectId,
                                  @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsStartDateForm form,
                                  Model model,
                                  BindingResult bindingResult,
                                  HttpServletRequest request) {
        ServiceResult<Void> updateResult = projectService.updateProjectStartDate(projectId, form.getProjectStartDate());
        return handleErrorsOrRedirectToProjectOverview("projectStartDate", projectId, model, form, bindingResult, updateResult, () -> viewStartDate(model, projectId, form, request));
    }

    @RequestMapping(value = "/{projectId}/details/project-address", method = RequestMethod.GET)
    public String viewAddress(Model model,
                              @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressViewModelForm form,
                              @PathVariable("projectId") final Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = loadDataIntoModel(project);
        if(project.getAddress() != null && project.getAddress().getId() != null && project.getAddress().getOrganisations().size() > 0) {
            RestResult<OrganisationAddressResource> result = organisationAddressRestService.findOne(project.getAddress().getOrganisations().get(0));
            if (result.isSuccess()) {
                form.setAddressType(OrganisationAddressType.valueOf(result.getSuccessObject().getAddressType().getName()));
            }
        }
        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    private String viewCurrentAddressForm(Model model, ProjectDetailsAddressViewModelForm form,
                                          ProjectResource project){
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = loadDataIntoModel(project);
        processAddressLookupFields(form);
        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    @RequestMapping(value = "/{projectId}/details/project-address", method = RequestMethod.POST)
    public String updateAddress(Model model,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressViewModelForm form,
                                BindingResult bindingResult,
                                @PathVariable("projectId") final Long projectId) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource leadOrganisation = getLeadOrganisation(projectResource.getApplication());
        if (bindingResult.hasErrors() && form.getAddressType() == null) {
            return viewCurrentAddressForm(model, form, projectResource);
        }
        AddressResource newAddressResource = null;
        OrganisationAddressType addressType = null;
        switch (form.getAddressType()) {
            case REGISTERED:
            case OPERATING:
            case PROJECT:
                Optional<OrganisationAddressResource> organisationAddressResource = getAddress(leadOrganisation, form.getAddressType());
                if (organisationAddressResource.isPresent()) {
                    newAddressResource = organisationAddressResource.get().getAddress();
                }
                addressType = form.getAddressType();
                break;
            case ADD_NEW:
                form.getAddressForm().setTriedToSave(true);
                if (bindingResult.hasErrors()) {
                    return viewCurrentAddressForm(model, form, projectResource);
                }
                newAddressResource = form.getAddressForm().getSelectedPostcode();
                addressType = PROJECT;
                break;
            default:
                newAddressResource = null;
                break;
        }
        projectResource.setAddress(newAddressResource);
        ServiceResult<Void> updateResult = projectService.updateAddress(leadOrganisation.getId(), projectId, addressType, newAddressResource);
        return handleErrorsOrRedirectToProjectOverview("", projectId, model, form, bindingResult, updateResult, () -> viewAddress(model, form, projectId));
    }

    @RequestMapping(value = "/{projectId}/details/project-address", params = SEARCH_ADDRESS, method = RequestMethod.POST)
    public String searchAddress(Model model,
                                @PathVariable("projectId") Long projectId,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressViewModelForm form,
                                BindingResult bindingResult) {
        form.getAddressForm().setSelectedPostcodeIndex(null);
        form.getAddressForm().setTriedToSearch(true);
        form.setAddressType(OrganisationAddressType.valueOf(form.getAddressType().name()));
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    @RequestMapping(value = "/{projectId}/details/project-address", params = SELECT_ADDRESS, method = RequestMethod.POST)
    public String selectAddress(Model model,
                                @PathVariable("projectId") Long projectId,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressViewModelForm form) {
        form.getAddressForm().setSelectedPostcode(null);
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    @RequestMapping(value = "/{projectId}/details/project-address", params = MANUAL_ADDRESS, method = RequestMethod.POST)
    public String manualAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressViewModelForm form,
                                @PathVariable("projectId") Long projectId) {
        AddressForm addressForm = form.getAddressForm();
        addressForm.setManualAddress(true);
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    private String handleErrorsOrRedirectToProjectOverview(
            String fieldName, long projectId, Model model,
            BindingResultTarget form, BindingResult bindingResult,
            ServiceResult<?> result,
            Supplier<String> viewSupplier) {
        if (result.isFailure()) {
            bindAnyErrorsToField(result, fieldName, bindingResult, form);
            model.addAttribute(FORM_ATTR_NAME, form);
            return viewSupplier.get();
        }

        return redirectToProjectDetails(projectId);
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }

    private Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation, final OrganisationAddressType addressType) {
        return organisation.getAddresses().stream().filter(a -> OrganisationAddressType.valueOf(a.getAddressType().getName()).equals(addressType)).findFirst();
    }

    private OrganisationResource getLeadOrganisation(final Long applicationId){
        ApplicationResource application = applicationService.getById(applicationId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        return organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisation());
    }

    /**
     * Get the list of postcode options, with the entered postcode. Add those results to the form.
     */
    private void addAddressOptions(ProjectDetailsAddressViewModelForm projectDetailsAddressViewModelForm) {
        if (StringUtils.hasText(projectDetailsAddressViewModelForm.getAddressForm().getPostcodeInput())) {
            AddressForm addressForm = projectDetailsAddressViewModelForm.getAddressForm();
            addressForm.setPostcodeOptions(searchPostcode(projectDetailsAddressViewModelForm.getAddressForm().getPostcodeInput()));
            addressForm.setPostcodeInput(projectDetailsAddressViewModelForm.getAddressForm().getPostcodeInput());
        }
    }

    /**
     * if user has selected a address from the dropdown, get it from the list, and set it as selected.
     */
    private void addSelectedAddress(ProjectDetailsAddressViewModelForm projectDetailsAddressViewModelForm) {
        AddressForm addressForm = projectDetailsAddressViewModelForm.getAddressForm();
        if (StringUtils.hasText(addressForm.getSelectedPostcodeIndex()) && addressForm.getSelectedPostcode() == null) {
            addressForm.setSelectedPostcode(addressForm.getPostcodeOptions().get(Integer.parseInt(addressForm.getSelectedPostcodeIndex())));
        }
    }

    private List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>> addressLookupRestResult = addressRestService.doLookup(postcodeInput);
        return addressLookupRestResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(),
                addresses -> addresses);
    }

    private ProjectDetailsAddressViewModel loadDataIntoModel(final ProjectResource project){
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = new ProjectDetailsAddressViewModel(project);
        OrganisationResource leadOrganisation = getLeadOrganisation(project.getApplication());

        Optional<OrganisationAddressResource> registeredAddress = getAddress(leadOrganisation, REGISTERED);
        if(registeredAddress.isPresent()){
            projectDetailsAddressViewModel.setRegisteredAddress(registeredAddress.get().getAddress());
        }

        Optional<OrganisationAddressResource> operatingAddress = getAddress(leadOrganisation, OPERATING);
        if(operatingAddress.isPresent()){
            projectDetailsAddressViewModel.setOperatingAddress(operatingAddress.get().getAddress());
        }

        Optional<OrganisationAddressResource> projectAddress = getAddress(leadOrganisation, PROJECT);
        if(projectAddress.isPresent()){
            projectDetailsAddressViewModel.setProjectAddress(projectAddress.get().getAddress());
        }

        return projectDetailsAddressViewModel;
    }

    private void processAddressLookupFields(ProjectDetailsAddressViewModelForm form) {
        addAddressOptions(form);
        addSelectedAddress(form);
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
