package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.publiccontent.transactional.ContentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Controller for all content group actions.
 */
@RestController
@RequestMapping("/content-group")
public class ContentGroupController {

    @Value("${ifs.data.service.file.storage.publiccontentattachment.max.filesize.bytes}")
    private Long maxFilesizeBytesForPublicContentAttachment;

    @Value("${ifs.data.service.file.storage.publiccontentattachment.valid.media.types}")
    private List<String> validMediaTypesForPublicContentAttachment;

    @Autowired
    private ContentGroupService contentGroupService;

    @Autowired
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @PostMapping(value = "upload-file", produces = "application/json")
    public RestResult<Void> uploadFile(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                                 @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                                 @RequestParam(value = "contentGroupId") long contentGroupId,
                                                 @RequestParam(value = "filename", required = false) String originalFilename,
                                                 HttpServletRequest request) {

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForPublicContentAttachment, maxFilesizeBytesForPublicContentAttachment, request, (fileAttributes, inputStreamSupplier) ->
                contentGroupService.uploadFile(contentGroupId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @PostMapping("remove-file/{contentGroupId}")
    public RestResult<Void> removeFile(@PathVariable("contentGroupId") final Long contentGroupId) {
        return contentGroupService.removeFile(contentGroupId).toPostResponse();
    }



    @GetMapping("/get-file-contents/{contentGroupId}")
    public @ResponseBody ResponseEntity<Object> getFileContents(
            @PathVariable("contentGroupId") long contentGroupId) throws IOException {
        return fileControllerUtils.handleFileDownload(() -> contentGroupService.getFileContents(contentGroupId));
    }

    @GetMapping(value = "/get-file-details/{contentGroupId}", produces = "application/json")
    public RestResult<FileEntryResource> getFileEntryDetails(
            @PathVariable("contentGroupId") long contentGroupId) throws IOException {

        return contentGroupService.getFileDetails(contentGroupId).toGetResponse();
    }
}
