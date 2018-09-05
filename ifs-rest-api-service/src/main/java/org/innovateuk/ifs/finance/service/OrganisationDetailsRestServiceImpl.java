package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.project.finance.resource.FinanceCheckResource} related data.
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
