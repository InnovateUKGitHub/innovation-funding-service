package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.FormInputResponseFileAndContents;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileHeaderAttributes;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    @Qualifier("formInputResponseFileValidator")
    private FileHttpHeadersValidator fileValidator;

    @PostMapping(value = "/file", produces = "application/json")
    public RestResult<FormInputResponseFileEntryCreatedResponse> createFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) throws IOException {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) -> {
            FormInputResponseFileEntryResource formInputResponseFile = createFormInputResponseFileEntry(fileAttributes, formInputId, applicationId, processRoleId);
            ServiceResult<FormInputResponseFileEntryResource> uploadResult = applicationService.createFormInputResponseFileUpload(formInputResponseFile, inputStreamSupplier);
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

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) -> {
            FormInputResponseFileEntryResource formInputResponseFile = createFormInputResponseFileEntry(fileAttributes, formInputId, applicationId, processRoleId);
            return applicationService.updateFormInputResponseFileUpload(formInputResponseFile, inputStreamSupplier);
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
        ServiceResult<FormInputResponse> deleteResult = applicationService.deleteFormInputResponseFileUpload(compoundId);
        return deleteResult.toDeleteResponse();
    }

    private ServiceResult<FormInputResponseFileAndContents> doGetFile(long formInputId, long applicationId, long processRoleId) {
        FormInputResponseFileEntryId formInputResponseFileEntryId = new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId);
        return applicationService.getFormInputResponseFileUpload(formInputResponseFileEntryId);
    }

    private FormInputResponseFileEntryResource createFormInputResponseFileEntry(FileHeaderAttributes fileAttributes, long formInputId, long applicationId, long processRoleId) {
        FileEntryResource fileEntry = fileAttributes.toFileEntryResource();
        return new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);
    }
}
