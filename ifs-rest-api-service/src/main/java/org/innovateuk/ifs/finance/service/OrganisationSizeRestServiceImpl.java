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
public class OrganisationSizeRestServiceImpl extends BaseRestService implements OrganisationSizeRestService {
    private String restUrl = "/organisation-size";

    @Override
    public RestResult<List<OrganisationSizeResource>> getOrganisationSizes() {
        return getWithRestResult(restUrl, organisationSizeListType());
    }

}
