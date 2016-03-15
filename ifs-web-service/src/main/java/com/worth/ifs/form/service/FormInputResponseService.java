package com.worth.ifs.form.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Map;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface FormInputResponseService {
    List<FormInputResponse> getByApplication(Long applicationId);
    Map<Long, FormInputResponse> mapFormInputResponsesToFormInput(List<FormInputResponse> responses);
    List<String> save(Long userId, Long applicationId, Long formInputId, String value);
    RestResult<FileEntryResource> createFile(Long formInputId, Long applicationId, Long processRoleId, String contentType,
                                             Long contentLength, String originalFileName, byte[] file);
    RestResult<Void> removeFile(Long formInputId, Long applicationId, Long processRoleId);
    RestResult<ByteArrayResource> getFile(Long formInputId, Long applicationId, Long processRoleId);
}
