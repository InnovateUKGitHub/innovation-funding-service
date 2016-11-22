package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.organisationTypeResourceListType;

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