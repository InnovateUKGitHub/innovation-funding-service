package org.innovateuk.ifs.project.correspondenceaddress.controller;

import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.AddressLookupBaseController;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.correspondenceaddress.form.ProjectDetailsAddressForm;
import org.innovateuk.ifs.project.correspondenceaddress.viewmodel.ProjectDetailsAddressViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.innovateuk.ifs.address.form.AddressForm.FORM_ACTION_PARAMETER;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;

/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/project")
public class ProjectUKCorrespondenceAddressController extends AddressLookupBaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDetailsService projectDetailsService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @GetMapping("/{projectId}/details/project-address/UK")
    public String viewAddress(@PathVariable("projectId") final Long projectId,
                              Model model,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ProjectDetailsAddressForm form) {

        ProjectResource project = projectService.getById(projectId);
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = loadDataIntoModel(project);
        if (project.getAddress() != null) {
            form.getAddressForm().editAddress(project.getAddress());
        }

        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @PostMapping("/{projectId}/details/project-address/UK")
    public String updateAddress(@PathVariable("projectId") final Long projectId,
                                Model model,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                                @SuppressWarnings("unused") BindingResult bindingResult,
                                ValidationHandler validationHandler) {
        ProjectResource projectResource = projectService.getById(projectId);

        if (validationHandler.hasErrors()) {
            return viewCurrentAddressForm(model, form, projectResource);
        }
        projectResource.setAddress(form.getAddressForm().getSelectedAddress(this::searchPostcode));
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectResource.getId());
        ServiceResult<Void> updateResult = projectDetailsService.updateAddress(projectId, projectResource.getAddress());
        return updateResult.handleSuccessOrFailure(
                failure -> {
                    validationHandler.addAnyErrors(failure, asGlobalErrors());
                    return viewAddress(projectId, model, form);
                },
                success -> redirectToProjectDetails(projectId));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @PostMapping(value = "/{projectId}/details/project-address/UK", params = FORM_ACTION_PARAMETER)
    public String addressFormAction(@PathVariable("projectId") Long projectId,
                                Model model,
                                @ModelAttribute(FORM_ATTR_NAME) ProjectDetailsAddressForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler) {

        ProjectResource project = projectService.getById(projectId);
        form.getAddressForm().validateAction(bindingResult);
        if (validationHandler.hasErrors()) {
            return viewCurrentAddressForm(model, form, project);
        }

        AddressForm addressForm = form.getAddressForm();
        addressForm.handleAction(this::searchPostcode);

        return viewCurrentAddressForm(model, form, project);
    }

    private String viewCurrentAddressForm(Model model, ProjectDetailsAddressForm form,
                                          ProjectResource project) {
        ProjectDetailsAddressViewModel projectDetailsAddressViewModel = loadDataIntoModel(project);
        model.addAttribute("model", projectDetailsAddressViewModel);
        return "project/details-address";
    }

    private ProjectDetailsAddressViewModel loadDataIntoModel(final ProjectResource project) {
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        return new ProjectDetailsAddressViewModel(project, competition.isKtp());
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }

}
