package com.worth.ifs.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Competition;
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
public class CompetitionsService extends BaseServiceProvider {
    @Value("${ifs.data.service.rest.competition}")
    String competitionsRestURL;

    private final Log log = LogFactory.getLog(getClass());

    public Application getApplicationById(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        Application application = restTemplate.getForObject(dataRestServiceURL + competitionsRestURL + "/id/" + applicationId, Application.class);
        return application;
    }

    public List<Application> getApplicationsByUserId(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Application[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + competitionsRestURL + "/findByUser/" + userId, Application[].class);
        Application[] applications = responseEntity.getBody();
        return Arrays.asList(applications);
    }

    public List<Competition> getAll() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Competition[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + competitionsRestURL + "/findAll/", Competition[].class);
        Competition[] competitions = responseEntity.getBody();
        return Arrays.asList(competitions);
    }


}
