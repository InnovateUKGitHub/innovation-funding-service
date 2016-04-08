package com.worth.ifs.form.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.formInputResponseListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.stringsListType;

/**
 * ResponseRestServiceImpl is a utility for CRUD operations on {@link Response}'s.
 * This class connects to the {@link com.worth.ifs.application.controller.ResponseController}
 * through a REST call.
 */
@Service
public class FormInputResponseRestServiceImpl extends BaseRestService implements FormInputResponseRestService {

    @Value("${ifs.data.service.rest.forminputresponse}")
    String formInputResponseRestURL;

    @Override
    public RestResult<List<FormInputResponseResource>> getResponsesByApplicationId(Long applicationId) {
        return getWithRestResult(formInputResponseRestURL + "/findResponsesByApplication/" + applicationId, formInputResponseListType());
    }

    @Override
    public RestResult<List<String>> saveQuestionResponse(Long userId,
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
        return postWithRestResult(formInputResponseRestURL + "/saveQuestionResponse/", node, stringsListType());
    }

    @Override
    public RestResult<FileEntryResource> createFileEntry(long formInputId, long applicationId, long processRoleId, String contentType, long contentLength, String originalFilename, byte[] file) {

        String url = formInputResponseRestURL + "/file" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId +
                "&filename=" + originalFilename;

        final HttpHeaders headers = createHeader(contentType,  contentLength);

        return postWithRestResult(url, file, headers, FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeFileEntry(long formInputId, long applicationId, long processRoleId) {

        String url = formInputResponseRestURL + "/file" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId;

        return deleteWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<ByteArrayResource> getFile(long formInputId, long applicationId, long processRoleId) {
        String url = formInputResponseRestURL + "/file" +
                "?formInputId=" + formInputId +
                "&applicationId=" + applicationId +
                "&processRoleId=" + processRoleId;

        return getWithRestResult(url, ByteArrayResource.class);
    }

    private HttpHeaders createHeader(String contentType, long contentLength){
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        return headers;
    }
}
