package com.worth.ifs.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.service.BaseRestServiceProvider;
import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;

/**
 * ResponseRestServiceImpl is a utility for CRUD operations on {@link Response}'s.
 * This class connects to the {@link com.worth.ifs.application.controller.ResponseController}
 * through a REST call.
 */
@Service
public class FormInputResponseRestServiceImpl extends BaseRestServiceProvider implements FormInputResponseRestService {
    @Value("${ifs.data.service.rest.forminputresponse}")
    String responseRestURL;

    private final Log log = LogFactory.getLog(getClass());


    public List<FormInputResponse> getResponsesByApplicationId(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FormInputResponse[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + responseRestURL + "/findResponsesByApplication/" + applicationId, FormInputResponse[].class);
        FormInputResponse[] responses = responseEntity.getBody();
        return Arrays.asList(responses);
    }

    public List<String> saveQuestionResponse(Long userId, Long applicationId, Long formInputId, String value) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + responseRestURL + "/saveQuestionResponse/";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("userId", userId);
        node.put("applicationId", applicationId);
        node.put("formInputId", formInputId);
        node.put("value", HtmlUtils.htmlEscape(value));

        HttpEntity<String> entity = new HttpEntity<String>(node.toString(), getJSONHeaders());

        ResponseEntity<String[]> responseEntity = restTemplate.postForEntity(url, entity, String[].class);
        List<String> validatedResponse = Arrays.asList(responseEntity.getBody());

        return validatedResponse;
    }

    private Boolean handleResponseStatus(ResponseEntity<?> response) {
        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            return true;
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // nono... bad credentials
            log.info("Unauthorized.....");
            return false;
        }

        return false;
    }
}
