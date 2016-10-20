package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.OrganisationTypeResource;

import java.util.List;

public interface OrganisationTypeRestService {

    RestResult<OrganisationTypeResource> findOne(Long id);
    RestResult<List<OrganisationTypeResource>> getAll();

    RestResult<OrganisationTypeResource> getForOrganisationId(Long organisationId);
}