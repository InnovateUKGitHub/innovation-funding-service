package com.worth.ifs.user.service;

import com.worth.ifs.core.service.BaseServiceProvider;
import com.worth.ifs.user.domain.Organisation;
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
public class OrganisationService extends BaseServiceProvider {
    @Value("${ifs.data.service.rest.organisation}")
    String organisationRestURL;

    private final Log log = LogFactory.getLog(getClass());


    public List<Organisation> getOrganisationsByApplicationId(Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Organisation[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + organisationRestURL + "/findByApplicationId/" + applicationId, Organisation[].class);
        Organisation[] organisations = responseEntity.getBody();
        return Arrays.asList(organisations);
    }


}
