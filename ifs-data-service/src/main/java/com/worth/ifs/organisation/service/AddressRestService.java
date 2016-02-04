package com.worth.ifs.organisation.service;

import com.worth.ifs.organisation.resource.AddressResource;
import com.worth.ifs.security.NotSecured;

public interface AddressRestService {
    @NotSecured("REST Service")
    AddressResource findOne(Long id);
}