package org.innovateuk.ifs.project.correspondenceaddress.controller;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.AddressLookupBaseController;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.correspondenceaddress.form.ProjectInternationalCorrespondenceAddressForm;
import org.innovateuk.ifs.project.correspondenceaddress.viewmodel.ProjectInternationalCorrespondenceAddressViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;

@Controller
@RequestMapping("/project")
public class ProjectInternationalCorrespondenceAddressController extends AddressLookupBaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDetailsService projectDetailsService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @GetMapping("/{projectId}/details/project-address/international")
    public String viewAddress(@PathVariable("projectId") final Long projectId,
                              Model model,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ProjectInternationalCorrespondenceAddressForm form) {

        ProjectResource project = projectService.getById(projectId);
        ProjectInternationalCorrespondenceAddressViewModel viewModel = loadDataIntoModel(project);

        if(project.getAddress() != null) {
            form.populate(project.getAddress());
        }

        model.addAttribute("model", viewModel);
        return "project/international-address";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
    @PostMapping("/{projectId}/details/project-address/international")
    public String updateAddress(@PathVariable("projectId") final Long projectId,
                                Model model,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectInternationalCorrespondenceAddressForm form,
                                @SuppressWarnings("unused") BindingResult bindingResult,
                                ValidationHandler validationHandler) {
        ProjectResource projectResource = projectService.getById(projectId);



        if (validationHandler.hasErrors()) {
            return viewCurrentAddressForm(model, form, projectResource);
        }
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectResource.getId());
        ServiceResult<Void> updateResult = projectDetailsService.updateAddress(projectId, projectResource.getAddress());
        return updateResult.handleSuccessOrFailure(
                failure -> {
                    validationHandler.addAnyErrors(failure, asGlobalErrors());
                    return viewAddress(projectId, model, form);
                },
                success -> redirectToProjectDetails(projectId));
    }

    private String viewCurrentAddressForm(Model model, ProjectInternationalCorrespondenceAddressForm form,
                                          ProjectResource project) {
        ProjectInternationalCorrespondenceAddressViewModel viewModel = loadDataIntoModel(project);
        model.addAttribute("model", viewModel);
        return "project/international-address";
    }

    private ProjectInternationalCorrespondenceAddressViewModel loadDataIntoModel(final ProjectResource project) {
        return new ProjectInternationalCorrespondenceAddressViewModel(project);
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }

}
