package com.worth.ifs.form.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.service.ResponseRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.util.CollectionFunctions.simpleToMap;

/**
 * This class contains methods to retrieve and store {@link Response} related data,
 * through the RestService {@link ResponseRestService}.
 */
// TODO DW - INFUND-1555 - handle rest results
@Service
public class FormInputResponseServiceImpl implements FormInputResponseService {

    @Autowired
    private FormInputResponseRestService responseRestService;

    @Override
    public List<FormInputResponseResource> getByApplication(Long applicationId) {
        return responseRestService.getResponsesByApplicationId(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public Map<Long, FormInputResponseResource> mapFormInputResponsesToFormInput(List<FormInputResponseResource> responses) {
        return simpleToMap(
            responses,
            response -> response.getFormInput(),
            response -> response
        );
    }

    @Override
    public List<String> save(Long userId, Long applicationId, Long formInputId, String value, boolean ignoreEmpty) {
        return responseRestService.saveQuestionResponse(userId, applicationId, formInputId, value, ignoreEmpty).getSuccessObjectOrThrowException();
    }

    @Override
    public RestResult<FileEntryResource> createFile(Long formInputId, Long applicationId, Long processRoleId, String contentType, Long contentLength, String originalFileName, byte[] file) {
        return responseRestService.createFileEntry(formInputId, applicationId, processRoleId, contentType, contentLength, originalFileName, file);
    }

    @Override
    public RestResult<Void> removeFile(Long formInputId, Long applicationId, Long processRoleId) {
        return responseRestService.removeFileEntry(formInputId, applicationId, processRoleId);
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long formInputId, Long applicationId, Long processRoleId) {
        return responseRestService.getFile(formInputId, applicationId, processRoleId);
    }

    @Override
    public RestResult<List<FormInputResponseResource>> getByFormInputIdAndApplication(Long formInputId, Long applicationId) {
        return responseRestService.getByFormInputIdAndApplication(formInputId, applicationId);
    }
}
