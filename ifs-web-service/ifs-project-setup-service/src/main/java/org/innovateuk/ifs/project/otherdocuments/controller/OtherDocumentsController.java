package org.innovateuk.ifs.project.otherdocuments.controller;

import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.otherdocuments.OtherDocumentsService;
import org.innovateuk.ifs.project.otherdocuments.form.OtherDocumentsForm;
import org.innovateuk.ifs.project.otherdocuments.populator.OtherDocumentsViewModelPopulator;
import org.innovateuk.ifs.project.otherdocuments.viewmodel.OtherDocumentsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;
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
    private OtherDocumentsService otherDocumentsService;

    @Autowired
    private ProjectService projectService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping
    public String viewOtherDocumentsPage(@P("projectId")@PathVariable("projectId") Long projectId, Model model,
                                         UserResource loggedInUser) {

        OtherDocumentsForm form = new OtherDocumentsForm();
        return doViewOtherDocumentsPage(projectId, model, loggedInUser, form);
    }

    private String doViewOtherDocumentsPage(Long projectId, Model model, UserResource loggedInUser, OtherDocumentsForm form) {
        OtherDocumentsViewModel viewModel = populator.populate(projectId, loggedInUser);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        model.addAttribute("currentUser", loggedInUser);

        return "project/other-documents";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'SUBMIT_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/confirm")
    public String viewConfirmDocumentsPage(@P("projectId")@PathVariable("projectId") Long projectId, Model model,
                                           UserResource loggedInUser) {
        OtherDocumentsViewModel viewModel = populator.populate(projectId, loggedInUser);
        model.addAttribute("model", viewModel);
        model.addAttribute("currentUser", loggedInUser);

        return "project/other-documents-confirm";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/readonly")
    public String viewDocumentsPageAsReadOnly(@P("projectId")@PathVariable("projectId") Long projectId, Model model,
                                              UserResource loggedInUser) {

        if (isProjectManager(projectId, loggedInUser)) {
            return redirectToOtherDocumentsPage(projectId);
        } else {
            OtherDocumentsViewModel viewModel = populator.populate(projectId, loggedInUser);
            model.addAttribute("model", viewModel);
            model.addAttribute("currentUser", loggedInUser);
            model.addAttribute("readOnlyView", true);

            return "project/other-documents";
        }
    }

    private boolean isProjectManager(Long projectId, UserResource loggedInUser) {
        return projectService.isProjectManager(loggedInUser.getId(), projectId);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping("/submit")
    public String submitPartnerDocuments(Model model, @P("projectId")@PathVariable("projectId") final Long projectId) {
        if (otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)) {
            otherDocumentsService.setPartnerDocumentsSubmitted(projectId).getSuccessObjectOrThrowException();
        }

        return redirectToOtherDocumentsPage(projectId);
    }

    private String redirectToOtherDocumentsPage(Long projectId) {
        return format("redirect:/project/%s/partner/documents", projectId);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/collaboration-agreement")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadCollaborationAgreementFile(
            @P("projectId")@PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = otherDocumentsService.getCollaborationAgreementFile(projectId);
        final Optional<FileEntryResource> fileDetails = otherDocumentsService.getCollaborationAgreementFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(params = "uploadCollaborationAgreementClicked")
    public String uploadCollaborationAgreementFile(
            @P("projectId")@PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) OtherDocumentsForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "collaborationAgreement", form, () -> {
            MultipartFile file = form.getCollaborationAgreement();

            return otherDocumentsService.addCollaborationAgreementDocument(projectId, file.getContentType(), file.getSize(),
                    file.getOriginalFilename(), getMultipartFileBytes(file));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(params = "removeCollaborationAgreementClicked")
    public String removeCollaborationAgreementFile(@P("projectId")@PathVariable("projectId") final Long projectId,
                                                   @ModelAttribute(FORM_ATTR) OtherDocumentsForm form,
                                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                                   ValidationHandler validationHandler,
                                                   Model model,
                                                   UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "collaborationAgreement", form,
                () -> otherDocumentsService.removeCollaborationAgreementDocument(projectId));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/exploitation-plan")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadExploitationPlanFile(
            @P("projectId")@PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = otherDocumentsService.getExploitationPlanFile(projectId);
        final Optional<FileEntryResource> fileDetails = otherDocumentsService.getExploitationPlanFileDetails(projectId);
        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Long projectId, Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            throw new ObjectNotFoundException("Could not find Collaboration Agreement for project " + projectId, singletonList(projectId));
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(params = "uploadExploitationPlanClicked")
    public String uploadExploitationPlanFile(
            @P("projectId")@PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) OtherDocumentsForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "exploitationPlan", form, () -> {

            MultipartFile file = form.getExploitationPlan();

            return otherDocumentsService.addExploitationPlanDocument(projectId, file.getContentType(), file.getSize(),
                    file.getOriginalFilename(), getMultipartFileBytes(file));
        });
    }

    private String performActionOrBindErrorsToField(Long projectId, ValidationHandler validationHandler, Model model, UserResource loggedInUser, String fieldName, OtherDocumentsForm form, Supplier<FailingOrSucceedingResult<?, ?>> actionFn) {

        Supplier<String> successView = () -> redirectToOtherDocumentsPage(projectId);
        Supplier<String> failureView = () -> doViewOtherDocumentsPage(projectId, model, loggedInUser, form);

        return validationHandler.performActionOrBindErrorsToField(fieldName, failureView, successView, actionFn);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(params = "removeExploitationPlanClicked")
    public String removeExploitationPlanFile(@P("projectId")@PathVariable("projectId") final Long projectId,
                                             @ModelAttribute(FORM_ATTR) OtherDocumentsForm form,
                                             @SuppressWarnings("unused") BindingResult bindingResult,
                                             ValidationHandler validationHandler,
                                             Model model,
                                             UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "exploitationPlan", form,
                () -> otherDocumentsService.removeExploitationPlanDocument(projectId));
    }
}
