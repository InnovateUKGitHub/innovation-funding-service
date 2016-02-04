package com.worth.ifs.user.service;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.resource.OrganisationTypeResource;

public interface OrganisationTypeRestService {
    @NotSecured("REST Service")
    OrganisationTypeResource findOne(Long id);
}