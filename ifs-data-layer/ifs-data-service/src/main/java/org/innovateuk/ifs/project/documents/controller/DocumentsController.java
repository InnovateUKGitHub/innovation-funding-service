package org.innovateuk.ifs.project.documents.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.innovateuk.ifs.project.documents.transactional.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * This rest controller is used to handle project documents, i.e, upload, delete, submit, and so on.
 */
@RestController
@RequestMapping("/project/{projectId}/document")
public class DocumentsController {

    @Autowired
    private DocumentsService documentsService;

    public DocumentsController() {
    }

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Value("${ifs.data.service.file.storage.projectsetupdocuments.max.filesize.bytes}")
    private Long maxFileSizeBytesForProjectSetupDocuments;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @PostMapping(value = "/config/{documentConfigId}/upload", produces = "application/json")
    public RestResult<FileEntryResource> uploadDocument(@PathVariable("projectId") long projectId,
                                                        @PathVariable("documentConfigId") long documentConfigId,
                                                        @RequestHeader(value = "Content-Type", required = false) String contentType,
                                                        @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                                        @RequestParam(value = "filename", required = false) String originalFilename,
                                                        HttpServletRequest request) {

        List<String> validMediaTypesForDocument = documentsService.getValidMediaTypesForDocument(documentConfigId).getSuccess();

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename,
                                                    fileValidator, validMediaTypesForDocument, maxFileSizeBytesForProjectSetupDocuments, request,
                                                    (fileAttributes, inputStreamSupplier) ->
                   documentsService.createDocumentFileEntry(projectId, documentConfigId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @GetMapping("/config/{documentConfigId}/file-contents")
    @ResponseBody
    public ResponseEntity<Object> getFileContents(@PathVariable("projectId") long projectId,
                                                  @PathVariable("documentConfigId") long documentConfigId) throws IOException {

        return fileControllerUtils.handleFileDownload(() -> documentsService.getFileContents(projectId, documentConfigId));
    }

    @GetMapping(value = "/config/{documentConfigId}/file-entry-details", produces = "application/json")
    public RestResult<FileEntryResource> getFileEntryDetails(@PathVariable("projectId") long projectId,
                                                             @PathVariable("documentConfigId") long documentConfigId) throws IOException {

        return documentsService.getFileEntryDetails(projectId, documentConfigId).toGetResponse();
    }

    @DeleteMapping(value = "/config/{documentConfigId}/delete", produces = "application/json")
    public RestResult<Void> deleteDocument(@PathVariable("projectId") long projectId,
                                           @PathVariable("documentConfigId") long documentConfigId) throws IOException {

        return documentsService.deleteDocument(projectId, documentConfigId).toDeleteResponse();
    }

    @PostMapping("/config/{documentConfigId}/submit")
    public RestResult<Void> submitDocument(@PathVariable("projectId") long projectId,
                                           @PathVariable("documentConfigId") long documentConfigId) {

        return documentsService.submitDocument(projectId, documentConfigId).toPostResponse();
    }

    @PostMapping("/config/{documentConfigId}/decision")
    public RestResult<Void> documentDecision(@PathVariable("projectId") long projectId,
                                             @PathVariable("documentConfigId") long documentConfigId,
                                             @RequestBody final ProjectDocumentDecision decision) {

        return documentsService.documentDecision(projectId, documentConfigId, decision).toPostResponse();
    }
}