package org.innovateuk.ifs.threads.attachments;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    @Qualifier("postAttachmentValidator")
    private FileHttpHeadersValidator fileValidator;

    @Autowired
    private FileEntryService fileEntryService;

    @RequestMapping(value = "", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addAdditionalContractFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                fileEntryService.saveFile(fileAttributes.toFileEntryResource())
        );
    }
}
