package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.publiccontent.transactional.ContentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileDownload;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;

/**
 * Controller for all content group actions.
 */
@RestController
@RequestMapping("/content-group")
public class ContentGroupController {

    @Autowired
    private ContentGroupService contentGroupService;

    @Autowired
    @Qualifier("publicContentAttachmentValidator")
    private FileHttpHeadersValidator fileValidator;

    @PostMapping(value = "upload-file", produces = "application/json")
    public RestResult<Void> uploadFile(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                                 @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                                 @RequestParam(value = "contentGroupId") long contentGroupId,
                                                 @RequestParam(value = "filename", required = false) String originalFilename,
                                                 HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                contentGroupService.uploadFile(contentGroupId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @PostMapping("remove-file/{contentGroupId}")
    public RestResult<Void> removeFile(@PathVariable("contentGroupId") final Long contentGroupId) {
        return contentGroupService.removeFile(contentGroupId).toPostResponse();
    }



    @GetMapping("/get-file-contents/{contentGroupId}")
    public @ResponseBody ResponseEntity<Object> getFileContents(
            @PathVariable("contentGroupId") long contentGroupId) throws IOException {
        return handleFileDownload(() -> contentGroupService.getFileContents(contentGroupId));
    }

    @GetMapping(value = "/get-file-details/{contentGroupId}", produces = "application/json")
    public RestResult<FileEntryResource> getFileEntryDetails(
            @PathVariable("contentGroupId") long contentGroupId) throws IOException {

        return contentGroupService.getFileDetails(contentGroupId).toGetResponse();
    }
}
