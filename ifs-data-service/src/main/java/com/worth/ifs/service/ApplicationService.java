package com.worth.ifs.service;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
        Application application = restTemplate.getForObject("http://localhost:8090/"+applicationHandle+"/id/"+applicationId, Application.class);
        return application;
    }
}
