package org.innovateuk.ifs.threads.attachments.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.threads.attachments.service.AttachmentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileDownload;

public abstract class AttachmentController<R> {

    private final AttachmentsService<R> service;

    public AttachmentController(AttachmentsService<R> service) {
        this.service = service;
    }

    @GetMapping(value = "/{attachmentId}", produces = "application/json")
    public RestResult<R> find(@PathVariable("attachmentId") Long attachmentId) {
        return service.findOne(attachmentId).toGetResponse();
    }

    @PostMapping(value = "/{contextId}/upload", produces = "application/json")
    public RestResult<R> uploadFile(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                    @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                    @RequestParam(value = "filename", required = false) String originalFilename,
                                    @PathVariable("contextId") Long contextId,
                                    HttpServletRequest request)
    {
        return service.upload(contentType, contentLength, originalFilename, contextId, request).toPostCreateResponse();
    }

    @DeleteMapping(value = "/{attachmentId}", produces = "application/json")
    public RestResult<Void> deleteFile(@PathVariable("attachmentId") Long attachmentId) {
        return service.delete(attachmentId).toDeleteResponse();
    }

    @GetMapping(value = "/download/{attachmentId}", produces = "application/json")
    public @ResponseBody ResponseEntity<Object> downloadFile(@PathVariable("attachmentId") Long attachmentId) throws IOException {
        return handleFileDownload(() -> service.attachmentFileAndContents(attachmentId));
    }
}