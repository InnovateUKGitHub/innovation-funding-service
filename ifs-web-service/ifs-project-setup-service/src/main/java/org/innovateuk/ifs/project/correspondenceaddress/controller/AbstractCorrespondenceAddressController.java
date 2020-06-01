//package org.innovateuk.ifs.project.correspondenceaddress.controller;
//
//import org.innovateuk.ifs.commons.service.ServiceResult;
//import org.innovateuk.ifs.controller.ValidationHandler;
//import org.innovateuk.ifs.organisation.resource.OrganisationResource;
//import org.innovateuk.ifs.project.AddressLookupBaseController;
//import org.innovateuk.ifs.project.ProjectService;
//import org.innovateuk.ifs.project.resource.ProjectResource;
//import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//
//import javax.validation.Valid;
//import java.util.function.Supplier;
//
//import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
//
//public abstract class AbstractCorrespondenceAddressController<F, V> extends AddressLookupBaseController {
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private ProjectDetailsService projectDetailsService;
//
//    @GetMapping
//    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
//    public String viewAddress(@PathVariable("projectId") final Long projectId,
//                              Model model,
//                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) F form) {
//
//        ProjectResource project = projectService.getById(projectId);
//        getViewModel(projectId);
//        if (project.getAddress() != null) {
//            editAddress(form, projectId);
//        }
//
//        model.addAttribute("model", getViewModel(projectId));
//        return getView();
//    }
//
//    @PostMapping
//    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
//    public String updateAddress(@PathVariable("projectId") final Long projectId,
//                                Model model,
//                                @Valid @ModelAttribute(FORM_ATTR_NAME) F form,
//                                @SuppressWarnings("unused") BindingResult bindingResult,
//                                ValidationHandler validationHandler) {
//        ProjectResource project = projectService.getById(projectId);
//
//        if (validationHandler.hasErrors()) {
//            return viewCurrentAddressForm(model, form, project);
//        }
//        setAddress(form, projectId);
//
//        Supplier<String> failureHandler = () -> {
//            model.addAttribute("model", getViewModel(projectId));
//            return getView();
//        };
//
//        Supplier<String> successHandler = () -> {
//            return "redirect:/project/" + projectId + "/details";
//        };
//        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(project.getId());
//        ServiceResult<Void> updateResult = projectDetailsService.updateAddress(leadOrganisation.getId(), projectId, project.getAddress());
//        return updateResult.handleSuccessOrFailure(
//                failure -> {
//                    validationHandler.addAnyErrors(failure, asGlobalErrors());
//                    return viewAddress(projectId, model, form);
//                }, redirectToProjectDetails(projectId));
//    }
//
//
//
//    protected abstract V getViewModel(long projectId);
//    protected abstract V loadDataIntoModel(ProjectResource project);
//    protected abstract void editAddress(F form, long projectId);
//    protected abstract String getView();
//
//    protected abstract String viewCurrentAddressForm(Model model, F form, ProjectResource project);
//    protected abstract void setAddress(F form, long projectId );
//
//    private String redirectToProjectDetails(long projectId) {
//        return "redirect:/project/" + projectId + "/details";
//    }
//    }
