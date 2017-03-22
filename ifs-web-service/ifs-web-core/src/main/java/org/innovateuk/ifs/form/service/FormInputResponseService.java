package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Map;

/**
 * Interface for CRUD operations on {@link FormInputResponseResource} related data.
 */
public interface FormInputResponseService {
    List<FormInputResponseResource> getByApplication(Long applicationId);
    Map<Long, FormInputResponseResource> mapFormInputResponsesToFormInput(List<FormInputResponseResource> responses);
    ValidationMessages save(Long userId, Long applicationId, Long formInputId, String value, boolean ignoreEmpty);
    RestResult<FileEntryResource> createFile(Long formInputId, Long applicationId, Long processRoleId, String contentType,
                                             Long contentLength, String originalFileName, byte[] file);
    RestResult<Void> removeFile(Long formInputId, Long applicationId, Long processRoleId);
    RestResult<ByteArrayResource> getFile(Long formInputId, Long applicationId, Long processRoleId);
    RestResult<FormInputResponseFileEntryResource> getFileDetails(Long formInputId, Long applicationId, Long processRoleId);
    RestResult<List<FormInputResponseResource>> getByFormInputIdAndApplication(Long formInputId, Long applicationId);
    FormInputResponseResource getByApplicationIdAndQuestionName(long formInputId, String questionName);
    List<FormInputResponseResource> getByApplicationIdAndQuestionId(long applicationId, long questionId);
}
