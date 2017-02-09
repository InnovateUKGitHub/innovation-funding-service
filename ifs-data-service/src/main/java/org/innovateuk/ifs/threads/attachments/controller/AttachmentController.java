package org.innovateuk.ifs.threads.attachments.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.threads.attachments.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileDownload;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;
import static org.springframework.web.bind.annotation.RequestMethod.*;

public class AttachmentController {

    private FileEntryService fileEntryService;
    private DownloadService downloadService;
    private FileHttpHeadersValidator fileValidator;

    public AttachmentController(FileEntryService fileEntryService, DownloadService downloadService, FileHttpHeadersValidator fileValidator) {
        this.fileEntryService = fileEntryService;
        this.downloadService = downloadService;
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
            HttpServletRequest request)
    {
        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                fileEntryService.saveFile(fileAttributes.toFileEntryResource())
        );
    }

    @RequestMapping(value = "/{fileId}", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteFile(@PathVariable("fileId") Long fileId) {
        return fileEntryService.removeFile(fileId).toDeleteResponse();
    }

    @RequestMapping(value = "/download/{fileId}", method = GET, produces = "application/json")
    public @ResponseBody ResponseEntity<Object> downloadFile(@PathVariable("fileId") Long fileId) throws IOException {
        return handleFileDownload(() -> fileEntryService.findOne(fileId)
                .andOnSuccess(f -> downloadService.getFileAndContents(f)));
    }
}