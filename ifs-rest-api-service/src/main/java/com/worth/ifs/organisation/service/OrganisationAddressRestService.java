package com.worth.ifs.organisation.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;

public interface OrganisationAddressRestService {

    RestResult<OrganisationAddressResource> findOne(Long id);
}