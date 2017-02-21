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
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.ifs.threads.attachments.service.AttachmentsService;
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

    AttachmentsService<Attachment> service;

    public AttachmentController(AttachmentsService<Attachment> service) {
        this.service = service;
    }

    @RequestMapping(value = "/{attachmentId}", method = GET, produces = "application/json")
    public RestResult<Attachment> find(@PathVariable("attachmentId") Long attachmentId) {
        return service.findOne(attachmentId).toGetResponse();
    }

    @RequestMapping(value = "/upload", method = POST, produces = "application/json")
    public RestResult<Attachment> uploadFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {
        return service.upload(contentType, contentLength, originalFilename, request).toPostCreateResponse();
    }

    @RequestMapping(value = "/{attachmentId}", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteFile(@PathVariable("attachmentId") Long attachmentId) {
        return service.delete(attachmentId).toDeleteResponse();
    }

    @RequestMapping(value = "/download/{attachmentId}", method = GET, produces = "application/json")
    public
    @ResponseBody
    ResponseEntity<Object> downloadFile(@PathVariable("attachmentId") Long attachmentId) throws IOException {
        return service.download(attachmentId);
    }
}