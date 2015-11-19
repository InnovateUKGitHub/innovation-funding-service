package com.worth.ifs.user.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.domain.Organisation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * OrganisationRestServiceImpl is a utility for CRUD operations on {@link Organisation}.
 * This class connects to the {@link com.worth.ifs.user.controller.OrganisationController}
 * through a REST call.
 */
@Service
public class OrganisationRestServiceImpl extends BaseRestService implements OrganisationRestService {
    @Value("${ifs.data.service.rest.organisation}")
    String organisationRestURL;

    private final Log log = LogFactory.getLog(getClass());

    public List<Organisation> getOrganisationsByApplicationId(Long applicationId) {
        return asList(restGet(organisationRestURL + "/findByApplicationId/" + applicationId, Organisation[].class));
    }

}
