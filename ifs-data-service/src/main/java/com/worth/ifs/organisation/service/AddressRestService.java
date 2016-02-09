package com.worth.ifs.organisation.service;

import com.worth.ifs.organisation.resource.AddressResource;

public interface AddressRestService {
    AddressResource findOne(Long id);
}