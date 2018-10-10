package org.innovateuk.ifs.project.documents.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.project.documents.transactional.DocumentsService;
import org.innovateuk.ifs.project.otherdocuments.transactional.OtherDocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/project/{projectId}/document")
public class DocumentsController {

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Value("${ifs.data.service.file.storage.projectsetupdocuments.max.filesize.bytes}")
    private Long maxFileSizeBytesForProjectSetupDocuments;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @PostMapping(value = "/config/{documentConfigId}/upload", produces = "application/json")
    public RestResult<FileEntryResource> uploadDocument(@PathVariable(value = "projectId") long projectId,
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
}
