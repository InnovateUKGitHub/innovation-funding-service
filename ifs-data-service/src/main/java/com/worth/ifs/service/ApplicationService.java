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

    public void saveApplication(Application application){
        log.warn("ApplicationService.saveApplication");

        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + applicationRestURL + "/saveApplicationDetails/" +application.getId();

        //set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        log.warn("ApplicationService.saveApplication add application to httpEntity");

        //set your entity to send
        HttpEntity entity = new HttpEntity(application,headers);

        log.warn("ApplicationService.saveApplication send it!");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity
                , String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("ApplicationService, save == ok : "+ response.getBody());
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // nono... bad credentials
            log.info("Unauthorized.....");
        }

    }
}
