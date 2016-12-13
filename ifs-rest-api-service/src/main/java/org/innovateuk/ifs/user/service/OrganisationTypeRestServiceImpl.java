package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationTypeResourceListType;

@Service
public class OrganisationTypeRestServiceImpl extends BaseRestService implements OrganisationTypeRestService {

    private String restUrl = "/organisationtype";

    @Override
    public RestResult<OrganisationTypeResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, OrganisationTypeResource.class);
    }

    @Override
    public RestResult<List<OrganisationTypeResource>> getAll() {
        return getWithRestResult(restUrl + "/getAll", organisationTypeResourceListType());
    }

    @Override
    public RestResult<OrganisationTypeResource> getForOrganisationId(Long organisationId) {
        return getWithRestResult(restUrl + "/getTypeForOrganisation/" + organisationId, OrganisationTypeResource.class);
    }
}
