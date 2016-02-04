package com.worth.ifs.organisation.transactional;

import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.security.NotSecured;

public interface AddressService {
    @NotSecured("TODO")
    Address findOne(Long id);
}