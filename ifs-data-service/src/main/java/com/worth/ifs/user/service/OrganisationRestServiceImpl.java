package com.worth.ifs.user.service;

import com.worth.ifs.commons.service.BaseRestServiceProvider;
import com.worth.ifs.user.domain.Organisation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * OrganisationRestServiceImpl is a utility for CRUD operations on {@link Organisation}.
 * This class connects to the {@link com.worth.ifs.user.controller.OrganisationController}
 * through a REST call.
 */
@Service
public class OrganisationRestServiceImpl extends BaseRestServiceProvider implements OrganisationRestService {
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
