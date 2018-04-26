package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.transactional.ApplicationFormInputUploadService;
import org.innovateuk.ifs.application.transactional.FormInputResponseFileAndContents;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileHeaderAttributes;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.innovateuk.ifs.file.controller.FileControllerUtils.*;

/**
 *
 */
@RestController
@RequestMapping("/forminputresponse")
public class FormInputResponseFileUploadController {

    @Value("${ifs.data.service.file.storage.forminputresponse.max.filesize.bytes}")
    private Long maxFilesizeBytesForFormInputResponses;

    @Autowired
    private ApplicationFormInputUploadService applicationFormInputUploadService;

    @Autowired
    @Qualifier("formInputFileValidator")
    private FilesizeAndTypeFileValidator<Long> fileValidator;

    @PostMapping(value = "/file", produces = "application/json")
    public RestResult<FormInputResponseFileEntryCreatedResponse> createFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) throws IOException {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, formInputId, maxFilesizeBytesForFormInputResponses, request, (fileAttributes, inputStreamSupplier) -> {
            FormInputResponseFileEntryResource formInputResponseFile = createFormInputResponseFileEntry(fileAttributes, formInputId, applicationId, processRoleId);
            ServiceResult<FormInputResponseFileEntryResource> uploadResult = applicationFormInputUploadService.createFormInputResponseFileUpload(formInputResponseFile, inputStreamSupplier);
            return uploadResult.andOnSuccessReturn(file -> new FormInputResponseFileEntryCreatedResponse(file.getFileEntryResource().getId()));
        });
    }

    @PutMapping(value = "/file", produces = "application/json")
    public RestResult<Void> updateFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) throws IOException {

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, formInputId, maxFilesizeBytesForFormInputResponses, request, (fileAttributes, inputStreamSupplier) -> {
            FormInputResponseFileEntryResource formInputResponseFile = createFormInputResponseFileEntry(fileAttributes, formInputId, applicationId, processRoleId);
            return applicationFormInputUploadService.updateFormInputResponseFileUpload(formInputResponseFile, inputStreamSupplier);
        });
    }

    @GetMapping("/file")
    public @ResponseBody ResponseEntity<Object> getFileContents(
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId) throws IOException {

        return handleFileDownload(() -> doGetFile(formInputId, applicationId, processRoleId));
    }

    @GetMapping(value = "/fileentry", produces = "application/json")
    public RestResult<FormInputResponseFileEntryResource> getFileEntryDetails(
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId) throws IOException {

        ServiceResult<FormInputResponseFileAndContents> result = doGetFile(formInputId, applicationId, processRoleId);
        return result.andOnSuccessReturn(FormInputResponseFileAndContents::getFormInputResponseFileEntry).toGetResponse();
    }

    @DeleteMapping(value = "/file", produces = "application/json")
    public RestResult<Void> deleteFileEntry(
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId) throws IOException {

        FormInputResponseFileEntryId compoundId = new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId);
        ServiceResult<FormInputResponse> deleteResult = applicationFormInputUploadService.deleteFormInputResponseFileUpload(compoundId);
        return deleteResult.toDeleteResponse();
    }

    private ServiceResult<FormInputResponseFileAndContents> doGetFile(long formInputId, long applicationId, long processRoleId) {
        FormInputResponseFileEntryId formInputResponseFileEntryId = new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId);
        return applicationFormInputUploadService.getFormInputResponseFileUpload(formInputResponseFileEntryId);
    }

    private FormInputResponseFileEntryResource createFormInputResponseFileEntry(FileHeaderAttributes fileAttributes, long formInputId, long applicationId, long processRoleId) {
        FileEntryResource fileEntry = fileAttributes.toFileEntryResource();
        return new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);
    }
}
