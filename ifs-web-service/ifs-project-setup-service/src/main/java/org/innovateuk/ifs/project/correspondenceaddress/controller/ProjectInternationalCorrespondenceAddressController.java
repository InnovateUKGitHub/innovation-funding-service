package org.innovateuk.ifs.project.correspondenceaddress.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
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
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;

@Controller
@RequestMapping("/project/{projectId}/details/project-address/international")
@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
@SecuredBySpring(value = "Controller", description = "A international lead can access the project address in project setup stage",  securedType = ProjectInternationalCorrespondenceAddressController.class)
public class ProjectInternationalCorrespondenceAddressController extends AddressLookupBaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDetailsService projectDetailsService;

    @GetMapping
    public String viewAddress(@PathVariable("projectId") final Long projectId,
                              Model model,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ProjectInternationalCorrespondenceAddressForm form) {

        ProjectResource project = projectService.getById(projectId);

        if(project.getAddress() != null) {
            form.populate(project.getAddress());
        }

        model.addAttribute("model", new ProjectInternationalCorrespondenceAddressViewModel(project));
        return "project/international-address";
    }

    @PostMapping
    public String updateAddress(@PathVariable("projectId") final Long projectId,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectInternationalCorrespondenceAddressForm form,
                                @SuppressWarnings("unused") BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                Model model) {

        ProjectResource projectResource = projectService.getById(projectId);

        Supplier<String> failureView = () -> viewCurrentAddressForm(model, projectResource);

        return validationHandler.failNowOrSucceedWith(failureView, () ->{
            projectResource.setAddress(createAddressResource(form));
            ServiceResult<Void> updateResult = projectDetailsService.updateAddress(projectId, projectResource.getAddress());
            return updateResult.handleSuccessOrFailure(
                    failure -> {
                    validationHandler.addAnyErrors(failure, asGlobalErrors());
                    return viewAddress(projectId, model, form);
                },
                success -> redirectToProjectDetails(projectId));

        });
    }

    private String viewCurrentAddressForm(Model model, ProjectResource project) {

        model.addAttribute("model", new ProjectInternationalCorrespondenceAddressViewModel(project));
        return "project/international-address";
    }

    private String redirectToProjectDetails(long projectId) {
        return "redirect:/project/" + projectId + "/details";
    }

    private AddressResource createAddressResource(ProjectInternationalCorrespondenceAddressForm form) {
        return new AddressResource(
                form.getAddressLine1(),
                form.getAddressLine2(),
                form.getTown(),
                form.getCountry(),
                form.getZipCode());
    }
}
