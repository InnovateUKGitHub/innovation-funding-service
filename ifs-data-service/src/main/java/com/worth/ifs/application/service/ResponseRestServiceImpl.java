package com.worth.ifs.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.service.BaseRestServiceProvider;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ResponseRestServiceImpl is a utility for CRUD operations on {@link Response}'s.
 * This class connects to the {@link com.worth.ifs.application.controller.ResponseController}
 * through a REST call.
 */
@Service
public class ResponseRestServiceImpl extends BaseRestServiceProvider implements ResponseRestService {
    @Value("${ifs.data.service.rest.response}")
    String responseRestURL;

    private final Log log = LogFactory.getLog(getClass());


    public List<Response> getResponsesByApplicationId(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Response[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + responseRestURL + "/findResponsesByApplication/" + applicationId, Response[].class);
        Response[] responses = responseEntity.getBody();
        return Arrays.asList(responses);
    }

    public List<String> saveQuestionResponse(Long userId, Long applicationId, Long questionId, String value) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + responseRestURL + "/saveQuestionResponse/";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("userId", userId);
        node.put("applicationId", applicationId);
        node.put("questionId", questionId);
        node.put("value", HtmlUtils.htmlEscape(value));

        HttpEntity<String> entity = new HttpEntity<String>(node.toString(), getJSONHeaders());
        ResponseEntity<String[]> responseEntity = restTemplate.postForEntity(url, entity, String[].class);

//        ResponseEntity<Response[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + responseRestURL + "/findResponsesByApplication/" + applicationId, Response[].class);
//        ResponseEntity<ValidatedResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, ValidatedResponse.class);

        log.debug("got response entity");


        List<String> validatedResponse = Arrays.asList(responseEntity.getBody());

        //validatedResponse.setErrorCount(errorCount);
//        validatedResponse.setResponse(response);
        return validatedResponse;
    }

    @Override
    public Boolean saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + responseRestURL + "/saveQuestionResponse/{responseId}/assessorFeedback?assessorUserId={assessorUserId}&feedbackValue={feedbackValue}&feedbackText={feedbackText}";

        HttpEntity<String> entity = new HttpEntity<>(getJSONHeaders());
        ResponseEntity<JsonStatusResponse> response = restTemplate.exchange(url, HttpMethod.PUT, entity, JsonStatusResponse.class, responseId, assessorUserId, feedbackValue.orElse(""), feedbackText.orElse(""));
        return handleResponseStatus(response);
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
