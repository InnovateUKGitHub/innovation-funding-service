package com.worth.ifs.application.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.BaseRestServiceProvider;
import com.worth.ifs.user.domain.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * ApplicationRestServiceImpl is a utility for CRUD operations on {@link Application}.
 * This class connects to the {@link com.worth.ifs.application.controller.ApplicationController}
 * through a REST call.
 */
@Service
public class ApplicationRestServiceImpl  extends BaseRestServiceProvider implements ApplicationRestService {
    @Value("${ifs.data.service.rest.application}")
    String applicationRestURL;

    private final Log log = LogFactory.getLog(getClass());

    public Application getApplicationById(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        Application application = restTemplate.getForObject(dataRestServiceURL + applicationRestURL + "/id/" + applicationId, Application.class);
        return application;
    }

    @Override
    public List<Application> getApplicationsByUserId(Long userId) {
        return asList(restGet(applicationRestURL + "/findByUser/" + userId, Application[].class));
    }

    public void saveApplication(Application application) {
        log.info("ApplicationRestRestService.saveApplication " + application.getId());

        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + applicationRestURL + "/saveApplicationDetails/" + application.getId();

        HttpEntity<Application> entity = new HttpEntity<>(application, getJSONHeaders());
        log.info("ApplicationRestRestService.saveApplication send it!");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity
                , String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("ApplicationRestRestService, save == ok : " + response.getBody());
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            //  bad credentials?
            log.info("Unauthorized request.");
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.info("Status code not_found .....");
        }

    }

    public void updateApplicationStatus(Long applicationId, Long statusId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + applicationRestURL + "/updateApplicationStatus?applicationId={applicationId}&statusId={statusId}";

        HttpEntity<String> entity = new HttpEntity<>("", getJSONHeaders());

        log.info("ApplicationRestRestService.updateApplicationStatus send it!");
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, applicationId, statusId);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("ApplicationRestRestService, save == ok : " + response.getBody());
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            //  bad credentials?
            log.info("Unauthorized request.");
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.info("Status code not_found .....");
        }
    }

    public Double getCompleteQuestionsPercentage(Long applicationId) {
        if (applicationId == null) {
            log.error("No application and/org organisation id!!");
        }

        ObjectNode jsonResponse = restGet(applicationRestURL + "/getProgressPercentageByApplicationId/" + applicationId, ObjectNode.class);
        return jsonResponse.get("completedPercentage").asDouble();
    }

    @Override
    public List<Application> getApplicationsByCompetitionIdAndUserId(Long competitionID, Long userID, UserRoleType role) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + applicationRestURL + "/getApplicationsByCompetitionIdAndUserId/" + competitionID + "/" + userID + "/" + role;
        ResponseEntity<Application[]> responseEntity = restTemplate.getForEntity(url, Application[].class);
        Application[] applications = responseEntity.getBody();
        return asList(applications);
    }


}
