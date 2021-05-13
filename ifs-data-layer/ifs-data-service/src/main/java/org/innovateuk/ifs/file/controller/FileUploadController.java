package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FileUploadService;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file/file-upload")
public class FileUploadController {

    @Value("10485760")
    private Long maxFilesizeBytesForApplicationFinance;

    @Value("${ifs.data.service.file.upload.files.valid.media.types}")
    private List<String> validMediaTypesForApplicationFinance;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @PostMapping(value = "/add-file", produces = "application/json")
    public RestResult<FileEntryResource> addFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "fileType") String fileType,
            @RequestParam(value = "fileName", required = false) String originalFilename,
            HttpServletRequest request) {

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForApplicationFinance, maxFilesizeBytesForApplicationFinance, request, (fileAttributes, inputStreamSupplier) ->
                fileUploadService.createFileEntry(fileType, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @DeleteMapping(value = "/delete-file", produces = "application/json")
    public RestResult<Void> deleteFileEntry(@RequestParam("uploadId") long uploadId) throws IOException {
        ServiceResult<Void> deleteResult = fileUploadService.deleteFileEntry(uploadId);
        return deleteResult.toDeleteResponse();
    }

    @GetMapping("/getFileAndContents")
    public @ResponseBody
    ResponseEntity<Object> getFileContent(@RequestParam("fileEntryId") long fileEntryId) throws IOException {
        return fileControllerUtils.handleFileDownload(() -> fileUploadService.getFileContents(fileEntryId));
    }

    @GetMapping("/get-file/fileentry")
    public RestResult<FileEntryResource> getFileDetails(@RequestParam("uploadId") long uploadId) throws IOException {
        return fileUploadService.getFileContents(uploadId).
                andOnSuccessReturn(FileAndContents::getFileEntry).
                toGetResponse();
    }

    @GetMapping("/get-allFiles")
    public RestResult<List<FileEntryResource>> getAllUploadedFileEntryResources() {
        return fileUploadService.getAllUploadedFileEntryResources().toGetResponse();
    }
}
