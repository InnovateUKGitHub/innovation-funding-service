package org.innovateuk.ifs.project.documents.controller;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.documents.form.DocumentForm;
import org.innovateuk.ifs.project.documents.populator.DocumentsPopulator;
import org.innovateuk.ifs.project.documents.service.DocumentsRestService;
import org.innovateuk.ifs.project.otherdocuments.form.OtherDocumentsForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * Controller backing the Documents page
 */
@Controller
@RequestMapping("/project/{projectId}/document")
public class DocumentsController {

    private static final String FORM_ATTR = "form";

    @Autowired
    DocumentsPopulator populator;

    @Autowired
    private DocumentsRestService documentsRestService;

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/all")
    public String viewAllDocuments(@PathVariable("projectId") long projectId, Model model,
                                   UserResource loggedInUser) {

        model.addAttribute("model", populator.populateAllDocuments(projectId));
        return "project/documents-all";
    }
    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/config/{documentConfigId}")
    public String viewDocument(@PathVariable("projectId") long projectId,
                               @PathVariable("documentConfigId") long documentConfigId,
                               Model model,
                               UserResource loggedInUser) {

        return doViewDocument(projectId, documentConfigId, model, loggedInUser, new DocumentForm());
    }

    private String doViewDocument(long projectId, long documentConfigId, Model model, UserResource loggedInUser, DocumentForm form) {

        model.addAttribute("model", populator.populateViewDocument(projectId, documentConfigId, loggedInUser));
        model.addAttribute(FORM_ATTR, form);
        return "project/document";
    }

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(value = "/config/{documentConfigId}", params = "uploadDocument")
    public String uploadDocument(@PathVariable("projectId") long projectId,
                                 @PathVariable("documentConfigId") long documentConfigId,
                                 @ModelAttribute(FORM_ATTR) DocumentForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, documentConfigId, validationHandler, model, loggedInUser, "document", form, () -> {

            MultipartFile file = form.getDocument();

            return documentsRestService.uploadDocument(projectId, documentConfigId, file.getContentType(), file.getSize(),
                    file.getOriginalFilename(), getMultipartFileBytes(file));
        });
    }

    private String performActionOrBindErrorsToField(long projectId, long documentConfigId, ValidationHandler validationHandler, Model model,
                                                    UserResource loggedInUser, String fieldName, DocumentForm form,
                                                    Supplier<FailingOrSucceedingResult<?, ?>> actionFn) {

        Supplier<String> successView = () -> redirectToViewDocumentPage(projectId, documentConfigId);
        Supplier<String> failureView = () -> doViewDocument(projectId, documentConfigId, model, loggedInUser, form);

        return validationHandler.performActionOrBindErrorsToField(fieldName, failureView, successView, actionFn);
    }

    private String redirectToViewDocumentPage(long projectId, long documentConfigId) {
        return format("redirect:/project/%s/document/config/%s", projectId, documentConfigId);
    }

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/config/{documentConfigId}/download")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable("projectId") long projectId,
                                                              @PathVariable("documentConfigId") long documentConfigId) {

        final Optional<ByteArrayResource> fileContents = documentsRestService.getFileContents(projectId, documentConfigId).getSuccess();
        final Optional<FileEntryResource> fileEntryDetails = documentsRestService.getFileEntryDetails(projectId, documentConfigId).getSuccess();
        return returnFileIfFoundOrThrowNotFoundException(projectId, documentConfigId, fileContents, fileEntryDetails);
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(long projectId, long documentConfigId,
                                                                                        Optional<ByteArrayResource> fileContents, Optional<FileEntryResource> fileEntryDetails) {
        if (fileContents.isPresent() && fileEntryDetails.isPresent()) {
            return getFileResponseEntity(fileContents.get(), fileEntryDetails.get());
        } else {
            throw new ObjectNotFoundException("Could not find document with document config id: " + documentConfigId + " for project: " + projectId, asList(projectId, documentConfigId));
        }
    }

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(value = "/config/{documentConfigId}", params = "deleteDocument")
    public String deleteDocument(@PathVariable("projectId") long projectId,
                                 @PathVariable("documentConfigId") long documentConfigId,
                                 @ModelAttribute(FORM_ATTR) DocumentForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, documentConfigId, validationHandler, model, loggedInUser, "document", form,
                () -> documentsRestService.deleteDocument(projectId, documentConfigId));
    }

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping(value = "/config/{documentConfigId}", params = "submitDocument")
    public String submitDocument(@PathVariable("projectId") long projectId,
                                 @PathVariable("documentConfigId") long documentConfigId,
                                 @ModelAttribute(FORM_ATTR) DocumentForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 UserResource loggedInUser) {

        Supplier<String> successView = () -> redirectToViewDocumentPage(projectId, documentConfigId);
        Supplier<String> failureView = () -> doViewDocument(projectId, documentConfigId, model, loggedInUser, form);

        RestResult<Void> result = documentsRestService.submitDocument(projectId, documentConfigId);

        return validationHandler.addAnyErrors(result, asGlobalErrors()).
                failNowOrSucceedWith(failureView, successView);
    }
}


