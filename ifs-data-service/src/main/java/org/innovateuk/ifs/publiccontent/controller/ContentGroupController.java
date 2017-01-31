package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.publiccontent.transactional.ContentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    @Qualifier("overheadCalculationFileValidator")
    private FileHttpHeadersValidator fileValidator;

    @RequestMapping(value = "upload-file/{contentGroupId}/{fileName}", method = RequestMethod.POST)
    public RestResult<Void> uploadFile(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                                 @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                                 @RequestParam(value = "contentGroupId") long contentGroupId,
                                                 @RequestParam(value = "fileName", required = false) String originalFilename,
                                                 HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                contentGroupService.uploadFile(contentGroupId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @RequestMapping(value = "remove-file/{contentGroupId}", method = RequestMethod.POST)
    public RestResult<Void> removeFile(@PathVariable("contentGroupId") final Long contentGroupId) {
        return contentGroupService.removeFile(contentGroupId).toPostResponse();
    }
}
