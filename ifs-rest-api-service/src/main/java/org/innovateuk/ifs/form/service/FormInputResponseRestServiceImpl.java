package org.innovateuk.ifs.form.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.formInputResponseListType;

/**
 * ResponseRestServiceImpl is a utility for CRUD operations on {@link FormInputResponseResource}'s.
 */
@Service
public class FormInputResponseRestServiceImpl extends BaseRestService implements FormInputResponseRestService {

    private String formInputResponseRestURL = "/forminputresponse";

    @Override
    public RestResult<List<FormInputResponseResource>> getResponsesByApplicationId(long applicationId) {
        return getWithRestResult(formInputResponseRestURL + "/find-responses-by-application/" + applicationId, formInputResponseListType());
    }

    @Override
    public RestResult<ValidationMessages> saveQuestionResponse(long userId,
                                                               long applicationId,
                                                               long formInputId,
                                                               String value,
                                                               boolean ignoreEmpty) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("userId", userId);
        node.put("applicationId", applicationId);
        node.put("formInputId", formInputId);
        node.put("value", HtmlUtils.htmlEscape(value));
        node.put("ignoreEmpty", ignoreEmpty);
        return postWithRestResult(formInputResponseRestURL + "/save-question-response/", node, ValidationMessages.class);
    }

    @Override
    public RestResult<FileEntryResource> createFileEntry(long formInputId, long applicationId, long processRoleId, String contentType, long contentLength, String originalFilename, byte[] file) {

        String url = formInputResponseRestURL + "/file" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId +
                "&filename=" + originalFilename;

        final HttpHeaders headers = createFileUploadHeader(contentType, contentLength);

        return postWithRestResult(url, file, headers, FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeFileEntry(long formInputId, long applicationId, long processRoleId, long fileEntryId) {

        String url = formInputResponseRestURL + "/file" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId +
                "&fileEntryId=" + fileEntryId;

        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> getFile(long formInputId, long applicationId, long processRoleId, long fileEntryId) {
        String url = formInputResponseRestURL + "/file" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId +
                "&fileEntryId=" + fileEntryId;

        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FormInputResponseFileEntryResource> getFileDetails(long formInputId, long applicationId, long processRoleId, long fileEntryId) {
        String url = formInputResponseRestURL + "/fileentry" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId +
                "&fileEntryId=" + fileEntryId;

        return getWithRestResult(url, FormInputResponseFileEntryResource.class);
    }

    @Override
    public RestResult<List<FormInputResponseResource>> getByFormInputIdAndApplication(long formInputId, long applicationId) {
        String url = formInputResponseRestURL + "/find-response-by-form-input-id-and-application-id/" + formInputId + "/" + applicationId;
        return getWithRestResult(url, formInputResponseListType());
    }

    @Override
    public RestResult<FormInputResponseResource> getByApplicationIdAndQuestionSetupType(long applicationId,
                                                                                        QuestionSetupType questionSetupType) {
        return getWithRestResult(format("%s/%s/%s/%s", formInputResponseRestURL, "find-by-application-id-and-question-setup-type",
                applicationId, questionSetupType), FormInputResponseResource.class);
    }

    @Override
    public RestResult<List<FormInputResponseResource>> getByApplicationIdAndQuestionId(long applicationId, long questionId) {
        return getWithRestResult(format("%s/%s/%s/%s", formInputResponseRestURL, "find-by-application-id-and-question-id",
                applicationId, questionId), formInputResponseListType());
    }
}
