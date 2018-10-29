package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.service.OrganisationAddressRestService;
import org.innovateuk.ifs.project.AddressLookupBaseController;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDetailsAddressForm;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsAddressViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.innovateuk.ifs.address.form.AddressForm.MANUAL_ADDRESS_PARAMETER;
import static org.innovateuk.ifs.address.form.AddressForm.SEARCH_POSTCODE_PARAMETER;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DETAILS_ADDRESS_SEARCH_OR_TYPE_MANUALLY;
import static org.innovateuk.ifs.commons.error.Error.fieldError;

/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/project")
public class ProjectDetailsAddressController extends AddressLookupBaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDetailsService projectDetailsService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @GetMapping("/{projectId}/details/project-address")
    public String viewAddress(@PathVariable("projectId") final Long projectId,
                              Model model,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ProjectDetailsAddressForm form) {

        ProjectResource project = projectService.getById(projectId);
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = loadDataIntoModel(project);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(project.getId());
        if (project.getAddress() != null && project.getAddress().getId() != null) {
            ProjectFinanceResource finance = projectFinanceService.getProjectFinance(projectId, leadOrganisation.getId());
            form.getAddressForm().setPostcodeInput(finance.getWorkPostcode());
        }

        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @PostMapping("/{projectId}/details/project-address")
    public String updateAddress(@PathVariable("projectId") final Long projectId,
                                Model model,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                                @SuppressWarnings("unused") BindingResult bindingResult,
                                ValidationHandler validationHandler) {
        ProjectResource projectResource = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, projectResource);
//
//
//
//
//
//
//
//ProjectResource projectResource = projectService.getById(projectId);
//
//
//        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectResource.getId());
//
//        if (validationHandler.hasErrors()) {
//            return viewCurrentAddressForm(model, form, projectResource);
//        }
//        projectResource.setAddress(form.getAddressForm().getSelectedAddress(() -> super.getOrganisationAddressResourceOrNull()));
//        ServiceResult<Void> updateResult = projectDetailsService.updateAddress(leadOrganisation.getId(), projectId, newAddressResource);
//
//        return updateResult.handleSuccessOrFailure(
//                failure -> {
//                    validationHandler.addAnyErrors(failure, asGlobalErrors());
//                    return viewAddress(projectId, model, form);
//                },
//                success -> redirectToProjectDetails(projectId));
    }
//
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @PostMapping(value = "/{projectId}/details/project-address", params = SEARCH_POSTCODE_PARAMETER)
    public String searchAddress(@PathVariable("projectId") Long projectId,
                                Model model,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                                BindingResult bindingResult) {
        AddressForm addressForm = form.getAddressForm();
        addressForm.setManualAddress(false);
        addressForm.setSearchPostcode(true);
        ProjectResource project = projectService.getById(projectId);
        addressForm.setPostcodeResults(searchPostcode(form.getAddressForm().getPostcodeInput()));
        return viewCurrentAddressForm(model, form, project);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @PostMapping(value = "/{projectId}/details/project-address", params = MANUAL_ADDRESS_PARAMETER)
    public String manualAddress(@PathVariable("projectId") Long projectId, Model model,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form) {
        AddressForm addressForm = form.getAddressForm();
        addressForm.setManualAddress(true);
        addressForm.setSearchPostcode(false);
        ProjectResource project = projectService.getById(projectId);
        return viewCurrentAddressForm(model, form, project);
    }

    private String viewCurrentAddressForm(Model model, ProjectDetailsAddressForm form,
                                          ProjectResource project) {
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = loadDataIntoModel(project);
        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    private ProjectDetailsAddressViewModel loadDataIntoModel(final ProjectResource project) {
        return new ProjectDetailsAddressViewModel(project);
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }

    private void addAddressNotProvidedValidationError(BindingResult bindingResult, ValidationHandler validationHandler) {
        ValidationMessages validationMessages = new ValidationMessages(bindingResult);
        validationMessages.addError(fieldError("addressType", new Error(PROJECT_SETUP_PROJECT_DETAILS_ADDRESS_SEARCH_OR_TYPE_MANUALLY)));
        validationHandler.addAnyErrors(validationMessages);
    }
}
