package org.innovateuk.ifs.project.otherdocuments.controller;

import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.otherdocuments.ProjectOtherDocumentsService;
import org.innovateuk.ifs.project.otherdocuments.form.OtherDocumentsForm;
import org.innovateuk.ifs.project.otherdocuments.populator.OtherDocumentsViewModelPopulator;
import org.innovateuk.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static java.util.Collections.singletonList;

/**
 * Controller backing the Other Documents page
 */
@Controller
@RequestMapping("/project/{projectId}/partner/documents")
public class OtherDocumentsController {

    private static final String FORM_ATTR = "form";

    @Autowired
    OtherDocumentsViewModelPopulator populator;

    @Autowired
    private ProjectOtherDocumentsService projectOtherDocumentsService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping
    public String viewOtherDocumentsPage(@PathVariable("projectId") Long projectId, Model model,
                                         @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {

        OtherDocumentsForm form = new OtherDocumentsForm();
        return doViewOtherDocumentsPage(projectId, model, loggedInUser, form);
    }

    private String doViewOtherDocumentsPage(Long projectId, Model model, UserResource loggedInUser, OtherDocumentsForm form) {
        ProjectOtherDocumentsViewModel viewModel = populator.populate(projectId, loggedInUser);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        model.addAttribute("currentUser", loggedInUser);

        return "project/other-documents";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/confirm")
    public String viewConfirmDocumentsPage(@PathVariable("projectId") Long projectId, Model model,
                                           @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {
        ProjectOtherDocumentsViewModel viewModel = populator.populate(projectId, loggedInUser);
        model.addAttribute("model", viewModel);
        model.addAttribute("currentUser", loggedInUser);

        return "project/other-documents-confirm";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/readonly")
    public String viewDocumentsPageAsReadOnly(@PathVariable("projectId") Long projectId, Model model,
                                              @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {

        ProjectOtherDocumentsViewModel viewModel = populator.populate(projectId, loggedInUser);
        model.addAttribute("model", viewModel);
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("readOnlyView", true);

        return "project/other-documents";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping("/submit")
    public String submitPartnerDocuments(Model model, @PathVariable("projectId") final Long projectId) {
        projectOtherDocumentsService.setPartnerDocumentsSubmitted(projectId).getSuccessObjectOrThrowException();
        return redirectToOtherDocumentsPage(projectId);
    }

    private String redirectToOtherDocumentsPage(Long projectId) {
        return "redirect:/project/" + projectId + "/partner/documents";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/collaboration-agreement")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadCollaborationAgreementFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectOtherDocumentsService.getCollaborationAgreementFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectOtherDocumentsService.getCollaborationAgreementFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(params = "uploadCollaborationAgreementClicked")
    public String uploadCollaborationAgreementFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) OtherDocumentsForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "collaborationAgreement", form, () -> {
            MultipartFile file = form.getCollaborationAgreement();

            return projectOtherDocumentsService.addCollaborationAgreementDocument(projectId, file.getContentType(), file.getSize(),
                    file.getOriginalFilename(), getMultipartFileBytes(file));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(params = "removeCollaborationAgreementClicked")
    public String removeCollaborationAgreementFile(@PathVariable("projectId") final Long projectId,
                                                   @ModelAttribute(FORM_ATTR) OtherDocumentsForm form,
                                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                                   ValidationHandler validationHandler,
                                                   Model model,
                                                   @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "collaborationAgreement", form,
                () -> projectOtherDocumentsService.removeCollaborationAgreementDocument(projectId));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/exploitation-plan")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadExploitationPlanFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectOtherDocumentsService.getExploitationPlanFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectOtherDocumentsService.getExploitationPlanFileDetails(projectId);
        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Long projectId, Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            throw new ObjectNotFoundException("Could not find Collaboration Agreement for project " + projectId, singletonList(projectId));
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(params = "uploadExploitationPlanClicked")
    public String uploadExploitationPlanFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) OtherDocumentsForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "exploitationPlan", form, () -> {

            MultipartFile file = form.getExploitationPlan();

            return projectOtherDocumentsService.addExploitationPlanDocument(projectId, file.getContentType(), file.getSize(),
                    file.getOriginalFilename(), getMultipartFileBytes(file));
        });
    }

    private String performActionOrBindErrorsToField(Long projectId, ValidationHandler validationHandler, Model model, UserResource loggedInUser, String fieldName, OtherDocumentsForm form, Supplier<FailingOrSucceedingResult<?, ?>> actionFn) {

        Supplier<String> successView = () -> redirectToOtherDocumentsPage(projectId);
        Supplier<String> failureView = () -> doViewOtherDocumentsPage(projectId, model, loggedInUser, form);

        return validationHandler.performActionOrBindErrorsToField(fieldName, failureView, successView, actionFn);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(params = "removeExploitationPlanClicked")
    public String removeExploitationPlanFile(@PathVariable("projectId") final Long projectId,
                                             @ModelAttribute(FORM_ATTR) OtherDocumentsForm form,
                                             @SuppressWarnings("unused") BindingResult bindingResult,
                                             ValidationHandler validationHandler,
                                             Model model,
                                             @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "exploitationPlan", form,
                () -> projectOtherDocumentsService.removeExploitationPlanDocument(projectId));
    }
}
