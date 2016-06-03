package com.worth.ifs.project;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.BindingResultTarget;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.ProjectDetailsAddressViewModel;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateForm;
import com.worth.ifs.project.viewmodel.ProjectDetailsStartDateViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.controller.RestFailuresToValidationErrorBindingUtils.bindAnyErrorsToField;

/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsController {
    public static final String REFERER = "referer";
    public static final String MANUAL_ADDRESS = "manual-address";
    public static final String SEARCH_ADDRESS = "search-address";
    public static final String SELECT_ADDRESS = "select-address";

    public static final String ADDRESS_USE_ORG = "address-use-org";
    public static final String ADDRESS_USE_OP = "address-use-operating";
    public static final String ADDRESS_USE_ADD = "address-add-project";

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
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;
    
    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private AddressService addressService;

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

        ServiceResult<Void> updateResult = projectService.updateProjectStartDate(projectId, form.getProjectStartDate());
        return handleErrorsOrRedirectToProjectOverview("projectStartDate", projectId, model, form, bindingResult, updateResult, () -> viewStartDate(model, projectId, form));
    }

    @RequestMapping(value = "/{projectId}/details/project-address", method = RequestMethod.GET)
    public String viewAddress(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute("form") ProjectDetailsAddressViewModel.ProjectDetailsAddressViewModelForm form) {
        ProjectResource project = projectService.getById(projectId);
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = new ProjectDetailsAddressViewModel(project);

        OrganisationResource leadOrganisation = getLeadOrganisation(projectId);
        List<Long> existingAddresses = new ArrayList<Long>();
        Optional<OrganisationAddressResource> registeredAddress = getAddress(leadOrganisation, AddressType.REGISTERED);
        if(registeredAddress.isPresent()){
            AddressResource addressResource = registeredAddress.get().getAddress();
            projectDetailsAddressViewModel.setRegisteredAddress(addressResource);
            existingAddresses.add(addressResource.getId());
            if(addressResource.getId().equals(project.getAddress())){
                form.setProjectAddressGroup(ADDRESS_USE_ORG);
            }
        }

        Optional<OrganisationAddressResource> operatingAddress = getAddress(leadOrganisation, AddressType.OPERATING);
        if(operatingAddress.isPresent()){
            AddressResource addressResource = operatingAddress.get().getAddress();
            projectDetailsAddressViewModel.setOperatingAddress(operatingAddress.get().getAddress());
            existingAddresses.add(addressResource.getId());
            if(addressResource.getId().equals(project.getAddress())){
                form.setProjectAddressGroup(ADDRESS_USE_OP);
            }
        }

        if(project.getAddress() != null && !existingAddresses.contains(project.getAddress())){
            AddressResource addressResource = addressService.getById(project.getAddress()).getSuccessObjectOrThrowException();
            projectDetailsAddressViewModel.setProjectAddress(addressResource);
            if(addressResource.getId().equals(project.getAddress())){
                form.setProjectAddressGroup(ADDRESS_USE_ADD);
            }
        }

        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    @RequestMapping(value = "/{projectId}/details/address", method = RequestMethod.POST)
    public String updateAddress(@PathVariable("projectId") final Long projectId,
                                  @ModelAttribute("form") ProjectDetailsAddressViewModel.ProjectDetailsAddressViewModelForm form,
                                  Model model,
                                  BindingResult bindingResult) {
        ProjectResource project = projectService.getById(projectId);
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = new ProjectDetailsAddressViewModel(project);
        OrganisationResource leadOrganisation = getLeadOrganisation(projectId);

        Long selectedAddressId = null;

        switch (form.getProjectAddressGroup()) {
            case ADDRESS_USE_OP:
                Optional<OrganisationAddressResource> operatingAddress = getAddress(leadOrganisation, AddressType.OPERATING);
                if (operatingAddress.isPresent()) {
                    selectedAddressId = operatingAddress.get().getAddress().getId();
                }
                break;
            case ADDRESS_USE_ORG:
                Optional<OrganisationAddressResource> registeredAddress = getAddress(leadOrganisation, AddressType.REGISTERED);
                if (registeredAddress.isPresent()) {
                    selectedAddressId = registeredAddress.get().getAddress().getId();
                }
                break;
            default:
                // Save new project address and assign id
                break;
        }
        ServiceResult<Void> updateResult = projectService.updateAddress(projectId, selectedAddressId);
        return handleErrorsOrRedirectToProjectOverview("projectStartDate", projectId, model, form, bindingResult, updateResult, () -> viewAddress(model, projectId, form));
    }

    /*@RequestMapping(value = "/{projectId}/details/address", params = SEARCH_ADDRESS, method = RequestMethod.POST)
    public String searchAddress(@ModelAttribute("form") ProjectDetailsAddressViewModel.ProjectDetailsAddressViewModelForm form,
                                Model model,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestHeader(value = REFERER, required = false) final String referer) {
        form.getAddressForm().setSelectedPostcodeIndex(null);
        form.getAddressForm().setTriedToSearch(true);
        return getRedirectUrlInvalidSave(organisationForm, referer);
    }*/

    private String handleErrorsOrRedirectToProjectOverview(
            String fieldName, long projectId, Model model,
            BindingResultTarget form, BindingResult bindingResult,
            ServiceResult<?> result,
            Supplier<String> viewSupplier) {
        if (result.isFailure()) {
            bindAnyErrorsToField(result, fieldName, bindingResult, form);
            model.addAttribute("form", form);
            return viewSupplier.get();
        }

        return redirectToProjectDetails(projectId);
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }

    private Optional<OrganisationAddressResource> getAddress(final OrganisationResource organisation, final AddressType addressType) {
        return organisation.getAddresses().stream().filter(a -> addressType.equals(a.getAddressType())).findFirst();
    }

    private OrganisationResource getLeadOrganisation(final Long projectId){
        ApplicationResource application = applicationService.getById(projectId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        return organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisation());
    }
}
