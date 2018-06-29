package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link FormInputResponseResource} related data.
 */
public interface FormInputResponseRestService {
    RestResult<List<FormInputResponseResource>> getResponsesByApplicationId(long applicationId);

    RestResult<ValidationMessages> saveQuestionResponse(long userId, long applicationId, long formInputId, String value, boolean ignoreEmpty);

    RestResult<FileEntryResource> createFileEntry(long formInputId, long applicationId, long processRoleId, String contentType, long contentLength, String originalFilename, byte[] file);

    RestResult<Void> removeFileEntry(long formInputId, long applicationId, long processRoleId);

    RestResult<ByteArrayResource> getFile(long formInputId, long applicationId, long processRoleId);

    RestResult<FormInputResponseFileEntryResource> getFileDetails(long formInputId, long applicationId, long processRoleId);

    RestResult<List<FormInputResponseResource>> getByFormInputIdAndApplication(long formInputId, long applicationId);

    RestResult<FormInputResponseResource> getByApplicationIdAndQuestionSetupType(long applicationId, QuestionSetupType questionSetupType);

    RestResult<List<FormInputResponseResource>> getByApplicationIdAndQuestionId(long applicationId, long questionId);
}
