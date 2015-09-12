package com.worth.ifs.application.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.core.service.BaseServiceProvider;
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
        log.info("ApplicationService.saveApplication "+ application.getId());

        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + applicationRestURL + "/saveApplicationDetails/" +application.getId();

        HttpEntity<Application> entity = new HttpEntity<>(application, getJSONHeaders());
        log.info("ApplicationService.saveApplication send it!");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity
                , String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("ApplicationService, save == ok : "+ response.getBody());
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            //  bad credentials?
            log.info("Unauthorized request.");
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.info("Status code not_found .....");
        }

    }

    public void updateApplicationStatus(Long applicationId, Long statusId){
        ///updateApplicationStatus/{id}/status/{statusId}

        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + applicationRestURL + "/updateApplicationStatus?applicationId={applicationId}&statusId={statusId}";

        HttpEntity<String> entity = new HttpEntity<>("", getJSONHeaders());

        log.info("ApplicationService.updateApplicationStatus send it!");
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, applicationId, statusId);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("ApplicationService, save == ok : "+ response.getBody());
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            //  bad credentials?
            log.info("Unauthorized request.");
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.info("Status code not_found .....");
        }
    }
    public Double getCompleteQuestionsPercentage(Long applicationId){
        //getProgressPercentageByApplicationId/{applicationId}
        if(applicationId == null){
            log.error("No application ID!! application id is null");
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + applicationRestURL + "/getProgressPercentageByApplicationId/{applicationId}";

        ObjectNode jsonResponse = restTemplate.getForObject(url, ObjectNode.class, applicationId);


        return jsonResponse.get("completedPercentage").asDouble();
    }
}
