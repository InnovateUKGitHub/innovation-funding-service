package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.springframework.stereotype.Service;

/**
 * Service for CRUD operations on {@link OrganisationSizeResource} related data.
 */
@Service
public class OrganisationDetailsRestServiceImpl extends BaseRestService implements OrganisationDetailsRestService {

    private String restUrl = "/project";

    @Override
    public RestResult<Long> getTurnover(Long applicationId, Long organisationId) {
        return getWithRestResult(restUrl + "/turnover/" + applicationId + "/" + organisationId, Long.TYPE);
    }

    @Override
    public RestResult<Long> getHeadCount(Long applicationId,  Long organisationId) {
        return getWithRestResult(restUrl + "/headcount/" + applicationId + "/" + organisationId, Long.TYPE);
    }

}
