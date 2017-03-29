package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationSizeListType;

/**
 * Service for CRUD operations on {@link OrganisationSizeResource} related data.
 */
@Service
public class OrganisationDetailsRestServiceImpl extends BaseRestService implements OrganisationDetailsRestService {
    private String restUrl = "/organisation-size";
    private String financeRestUrl = "/project";

    @Override
    public RestResult<List<OrganisationSizeResource>> getOrganisationSizes() {
        return getWithRestResult(restUrl, organisationSizeListType());
    }

    @Override
    public RestResult<Long> getTurnover(Long applicationId, Long organisationId) {
        return getWithRestResult(financeRestUrl + "/turnover/" + applicationId + "/" + organisationId, Long.TYPE);
    }

    @Override
    public RestResult<Long> getHeadCount(Long applicationId,  Long organisationId) {
        return getWithRestResult(financeRestUrl + "/headcount/" + applicationId + "/" + organisationId, Long.TYPE);
    }

}
