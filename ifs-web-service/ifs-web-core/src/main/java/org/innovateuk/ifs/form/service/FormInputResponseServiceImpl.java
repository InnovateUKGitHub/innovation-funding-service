package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * This class contains methods to retrieve and store {@link FormInputResponseResource} related data,
 * through the RestService {@link FormInputResponseRestService}.
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
    public ValidationMessages save(Long userId, Long applicationId, Long formInputId, String value, boolean ignoreEmpty) {
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
    public RestResult<FormInputResponseFileEntryResource> getFileDetails(Long formInputId, Long applicationId, Long processRoleId) {
        return responseRestService.getFileDetails(formInputId, applicationId, processRoleId);
    }

    @Override
    public RestResult<List<FormInputResponseResource>> getByFormInputIdAndApplication(Long formInputId, Long applicationId) {
        return responseRestService.getByFormInputIdAndApplication(formInputId, applicationId);
    }

    @Override
    public FormInputResponseResource getByApplicationIdAndQuestionName(long applicationId, String questionName) {
        return responseRestService.getByApplicationIdAndQuestionName(applicationId, questionName)
                .getSuccessObjectOrThrowException();
    }

    @Override
    public List<FormInputResponseResource> getByApplicationIdAndQuestionId(long applicationId, long questionId) {
        return responseRestService.getByApplicationIdAndQuestionId(applicationId, questionId)
                .getSuccessObjectOrThrowException();
    }
}
