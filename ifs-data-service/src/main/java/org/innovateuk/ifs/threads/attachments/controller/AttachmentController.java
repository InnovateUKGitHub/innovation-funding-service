package org.innovateuk.ifs.threads.attachments.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.file.transactional.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileDownload;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;
import static org.springframework.web.bind.annotation.RequestMethod.*;

public abstract class AttachmentController {

    private FileService fileService;
    private FileEntryService fileEntryService;
    private FileHttpHeadersValidator fileValidator;

    public AttachmentController(FileService fileService, FileEntryService fileEntryService, FileHttpHeadersValidator fileValidator) {
        this.fileService = fileService;
        this.fileEntryService = fileEntryService;
        this.fileValidator = fileValidator;
    }

    @RequestMapping(value = "/{fileId}", method = GET, produces = "application/json")
    public RestResult<FileEntryResource> find(@PathVariable("fileId") Long fileId) {
        return fileEntryService.findOne(fileId).toGetResponse();
    }

    @RequestMapping(value = "/upload", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> uploadFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {
        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                        .andOnSuccess(created -> fileEntryService.findOne(created.getRight().getId())));
    }

    @RequestMapping(value = "/{fileId}", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteFile(@PathVariable("fileId") Long fileId) {
        return fileEntryService.removeFile(fileId).toDeleteResponse();
    }

    @RequestMapping(value = "/download/{fileId}", method = GET, produces = "application/json")
    public
    @ResponseBody
    ResponseEntity<Object> downloadFile(@PathVariable("fileId") Long fileId) throws IOException {
        return handleFileDownload(() -> fileEntryService.findOne(fileId).andOnSuccess(f -> getFileAndContents(f)));
    }


    private ServiceResult<FileAndContents> getFileAndContents(FileEntryResource fileEntry) {
        if (fileEntry == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        }
        return fileService.getFileByFileEntryId(fileEntry.getId())
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream));
    }
}