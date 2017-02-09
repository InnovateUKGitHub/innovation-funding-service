package org.innovateuk.ifs.threads.attachments.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.threads.attachments.DownloadService;
import org.innovateuk.ifs.threads.attachments.controller.AttachmentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/project/finance/attachment")
public class ProjectFinancePostAttachmentsController extends AttachmentController {

    @Autowired
    public ProjectFinancePostAttachmentsController(FileService fileService, FileEntryService fileEntryService, DownloadService downloadService,
                                                   @Qualifier("postAttachmentValidator") FileHttpHeadersValidator fileValidator) {
        super(fileService, fileEntryService, downloadService, fileValidator);
    }

    @Override

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    public RestResult<FileEntryResource> find(@PathVariable("fileId") Long fileId) {
        return super.find(fileId);
    }

    @Override
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    public RestResult<FileEntryResource> uploadFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request)
    {
        return super.uploadFile(contentType, contentLength, originalFilename, request);
    }

    @Override
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    public RestResult<Void> deleteFile(@PathVariable("fileId") Long fileId) {
        return super.deleteFile(fileId);
    }

    @Override
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    public @ResponseBody ResponseEntity<Object> downloadFile(@PathVariable("fileId") Long fileId) throws IOException {
        return super.downloadFile(fileId);
    }
}
