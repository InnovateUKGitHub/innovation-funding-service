package com.worth.ifs.organisation.service;

import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.security.NotSecured;

public interface OrganisationAddressRestService {
    @NotSecured("REST Service")
    OrganisationAddressResource findOne(Long id);
}