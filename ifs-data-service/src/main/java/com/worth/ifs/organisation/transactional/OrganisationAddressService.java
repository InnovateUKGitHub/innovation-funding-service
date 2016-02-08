package com.worth.ifs.organisation.transactional;

import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.security.NotSecured;

public interface OrganisationAddressService {
    @NotSecured("TODO")
    OrganisationAddress findOne(Long id);
}