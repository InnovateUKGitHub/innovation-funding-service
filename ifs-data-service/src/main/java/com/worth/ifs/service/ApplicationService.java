package com.worth.ifs.service;

import com.worth.ifs.domain.Application;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wouter on 30/07/15.
 */
@Service
public class ApplicationService {
    String applicationHandle = "application";
    public Application getApplicationById(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        Application application = restTemplate.getForObject("http://localhost:8090/" + applicationHandle + "/id/" + applicationId, Application.class);
        return application;
    }

    public List<Application> getApplicationsByUserId(Long userId){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Application[]> responseEntity = restTemplate.getForEntity("http://localhost:8090/application/findByUser/"+userId, Application[].class);
        Application[] applications =responseEntity.getBody();
        return Arrays.asList(applications);
    }
}
