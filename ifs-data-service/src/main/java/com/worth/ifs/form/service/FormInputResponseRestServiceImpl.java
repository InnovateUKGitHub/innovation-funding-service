package com.worth.ifs.form.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.formInputResponseListType;

/**
 * ResponseRestServiceImpl is a utility for CRUD operations on {@link com.worth.ifs.form.domain.FormInputResponse}'s.
 * This class connects to the {@link com.worth.ifs.form.controller.FormInputResponseController}
 * through a REST call.
 */
@Service
public class FormInputResponseRestServiceImpl extends BaseRestService implements FormInputResponseRestService {

    private String formInputResponseRestURL = "/forminputresponse";

    @Override
    public RestResult<List<FormInputResponseResource>> getResponsesByApplicationId(Long applicationId) {
        return getWithRestResult(formInputResponseRestURL + "/findResponsesByApplication/" + applicationId, formInputResponseListType());
    }

    @Override
    public RestResult<ValidationMessages> saveQuestionResponse(Long userId,
                                                               Long applicationId,
                                                               Long formInputId,
                                                               String value,
                                                               boolean ignoreEmpty) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("userId", userId);
        node.put("applicationId", applicationId);
        node.put("formInputId", formInputId);
        node.put("value", HtmlUtils.htmlEscape(value));
        node.put("ignoreEmpty", ignoreEmpty);
        return postWithRestResult(formInputResponseRestURL + "/saveQuestionResponse/", node, ValidationMessages.class);
    }

    @Override
    public RestResult<FileEntryResource> createFileEntry(long formInputId, long applicationId, long processRoleId, String contentType, long contentLength, String originalFilename, byte[] file) {

        String url = formInputResponseRestURL + "/file" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId +
                "&filename=" + originalFilename;

        final HttpHeaders headers = createFileUploadHeader(contentType,  contentLength);

        return postWithRestResult(url, file, headers, FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeFileEntry(long formInputId, long applicationId, long processRoleId) {

        String url = formInputResponseRestURL + "/file" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId;

        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> getFile(long formInputId, long applicationId, long processRoleId) {
        String url = formInputResponseRestURL + "/file" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId;

        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FormInputResponseFileEntryResource> getFileDetails(long formInputId, long applicationId, long processRoleId) {
        String url = formInputResponseRestURL + "/fileentry" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId;

        return getWithRestResult(url, FormInputResponseFileEntryResource.class);
    }

    @Override
    public RestResult<List<FormInputResponseResource>> getByFormInputIdAndApplication(long formInputId, long applicationId) {
        String url = formInputResponseRestURL + "/findResponseByFormInputIdAndApplicationId/" + formInputId + "/" + applicationId;
        return getWithRestResult(url, formInputResponseListType());
    }
}
