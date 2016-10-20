package com.worth.ifs.form.service;

import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.form.domain.FormInputResponse} related data.
 */
public interface FormInputResponseRestService {
    RestResult<List<FormInputResponseResource>> getResponsesByApplicationId(Long applicationId);
    RestResult<ValidationMessages> saveQuestionResponse(Long userId, Long applicationId, Long formInputId, String value, boolean ignoreEmpty);
    RestResult<FileEntryResource> createFileEntry(long formInputId, long applicationId, long processRoleId, String contentType, long contentLength, String originalFilename, byte[] file);
    RestResult<Void> removeFileEntry(long formInputId, long applicationId, long processRoleId);
    RestResult<ByteArrayResource> getFile(long formInputId, long applicationId, long processRoleId);
    RestResult<FormInputResponseFileEntryResource> getFileDetails(long formInputId, long applicationId, long processRoleId);
    RestResult<List<FormInputResponseResource>> getByFormInputIdAndApplication(long formInputId, long applicationId);
}
