package org.innovateuk.ifs.application.forms.saver;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.exception.UnableToReadUploadedFile;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.REMOVE_UPLOADED_FILE;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;

/**
 * This Saver will handle all question uploads that are related to the application.
 */
@Service
public class ApplicationQuestionFileSaver extends AbstractApplicationSaver {

    private static final Log LOG = LogFactory.getLog(ApplicationQuestionFileSaver.class);

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    public ValidationMessages saveFileUploadQuestionsIfAny(List<QuestionResource> questions,
                                                           final Map<String, String[]> params,
                                                           HttpServletRequest request,
                                                           Long applicationId,
                                                           Long processRoleId) {
        ValidationMessages allErrors = new ValidationMessages();
        questions.forEach(question -> {
            List<FormInputResource> formInputs = formInputRestService.getByQuestionIdAndScope(question.getId(), APPLICATION).getSuccessObjectOrThrowException();
            formInputs.stream()
                    .filter(formInput1 -> FILEUPLOAD == formInput1.getType() && request instanceof MultipartHttpServletRequest)
                    .forEach(formInput ->
                            allErrors.addAll(processFormInput(formInput.getId(), params, applicationId, processRoleId, request))
                    );
        });
        return allErrors;
    }

    private ValidationMessages processFormInput(Long formInputId, Map<String, String[]> params, Long applicationId, Long processRoleId, HttpServletRequest request) {
        if (params.containsKey(REMOVE_UPLOADED_FILE)) {
            return removeUploadedFile(formInputId, applicationId, processRoleId);
        } else {
            return uploadFile(request, formInputId, applicationId, processRoleId);
        }
    }

    private ValidationMessages uploadFile(HttpServletRequest request, Long formInputId, Long applicationId, Long processRoleId) {
        final Map<String, MultipartFile> fileMap = ((MultipartHttpServletRequest) request).getFileMap();
        final MultipartFile file = fileMap.get(getFormInputKey(formInputId));
        if (file != null && !file.isEmpty()) {
            try {
                RestResult<FileEntryResource> result = formInputResponseRestService.createFileEntry(formInputId,
                        applicationId,
                        processRoleId,
                        file.getContentType(),
                        file.getSize(),
                        file.getOriginalFilename(),
                        file.getBytes());

                if (result.isFailure()) {

                    ValidationMessages errors = new ValidationMessages();
                    result.getFailure().getErrors().forEach(e ->
                            errors.addError(fieldError(getFormInputKey(formInputId), e.getFieldRejectedValue(), e.getErrorKey()))
                    );
                    return errors;
                }

            } catch (IOException e) {
                LOG.error(e);
                throw new UnableToReadUploadedFile();
            }
        }

        return noErrors();
    }

    private ValidationMessages removeUploadedFile(Long formInputId, Long applicationId, Long processRoleId) {
        formInputResponseRestService.removeFileEntry(formInputId, applicationId, processRoleId).getSuccessObjectOrThrowException();
        return noErrors();
    }
}
