package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/external-system-files")
public class FileUploadController {

    @Value("10485760")
    private Long maxFilesizeBytesForUpload;

    @Value("${ifs.data.service.file.upload.files.valid.media.types}")
    private List<String> validMediaTypes;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @PostMapping(value = "/upload-file", produces = "application/json")
    public RestResult<FileEntryResource> uploadFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "fileType") String fileType,
            @RequestParam(value = "fileName", required = false) String originalFilename,
            HttpServletRequest request) {

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypes, maxFilesizeBytesForUpload, request, (fileAttributes, inputStreamSupplier) ->
                fileUploadService.uploadFile(fileType, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }
}
