package com.worth.ifs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Response;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * ApplicationService is a utility to use client-side to retrieve Application data from the data-service controllers.
 */

@Service
public class ApplicationService extends BaseServiceProvider {
    @Value("${ifs.data.service.rest.application}")
    String applicationRestURL;

    private final Log log = LogFactory.getLog(getClass());

    public Application getApplicationById(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        Application application = restTemplate.getForObject(dataRestServiceURL + applicationRestURL + "/id/" + applicationId, Application.class);
        return application;
    }

    public List<Application> getApplicationsByUserId(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Application[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + applicationRestURL + "/findByUser/" + userId, Application[].class);
        Application[] applications = responseEntity.getBody();
        return Arrays.asList(applications);
    }

    public List<Response> getResponsesByApplicationId(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Response[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + applicationRestURL + "/findResponsesByApplication/" + applicationId, Response[].class);
        Response[] responses = responseEntity.getBody();
        return Arrays.asList(responses);
    }

    public void saveQuestionResponse(Long userId, Long applicationId, Long questionId, String value) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + applicationRestURL  + "/saveQuestionResponse/";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("userId", userId);
        node.put("applicationId", applicationId);
        node.put("questionId", questionId);
        node.put("value", value);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(node.toString(), headers);


        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("response json: "+ response.getBody());
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // nono... bad credentials
            log.info("Unauthorized.....");
        }

        return ;
    }
}
