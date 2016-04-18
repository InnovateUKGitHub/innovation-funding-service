package com.worth.ifs.form.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Map;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface FormInputResponseService {
    List<FormInputResponseResource> getByApplication(Long applicationId);
    Map<Long, FormInputResponseResource> mapFormInputResponsesToFormInput(List<FormInputResponseResource> responses);
    List<String> save(Long userId, Long applicationId, Long formInputId, String value, boolean ignoreEmpty);
    RestResult<FileEntryResource> createFile(Long formInputId, Long applicationId, Long processRoleId, String contentType,
                                             Long contentLength, String originalFileName, byte[] file);
    RestResult<Void> removeFile(Long formInputId, Long applicationId, Long processRoleId);
    RestResult<ByteArrayResource> getFile(Long formInputId, Long applicationId, Long processRoleId);
    RestResult<List<FormInputResponseResource>> getByFormInputIdAndApplication(Long formInputId, Long applicationId);
}
