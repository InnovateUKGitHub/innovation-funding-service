package com.worth.ifs.service;

import com.worth.ifs.domain.Application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    public Application getApplicationById(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        Application application = restTemplate.getForObject(dataRestServiceURL + applicationRestURL + "/id/" + applicationId, Application.class);
        return application;
    }

    public List<Application> getApplicationsByUserId(Long userId){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Application[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + applicationRestURL  + "/findByUser/"+userId, Application[].class);
        Application[] applications =responseEntity.getBody();
        return Arrays.asList(applications);
    }
}
