package org.innovateuk.ifs.project.documents.controller;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * Controller backing the Documents page
 */
@Controller
@RequestMapping("/project/{projectId}/document")
//TODO - XXX - Rename this class back to normal
public class ManagementDocumentsController {

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

        return doViewDocument(projectId, documentConfigId, model);
    }

    private String doViewDocument(long projectId, long documentConfigId, Model model) {

        model.addAttribute("model", populator.populateViewDocument(projectId, documentConfigId));
        return "project/document";
    }

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/config/{documentConfigId}/download")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable("projectId") long projectId,
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
}
