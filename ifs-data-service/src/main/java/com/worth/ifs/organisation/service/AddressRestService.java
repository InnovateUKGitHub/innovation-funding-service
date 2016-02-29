package com.worth.ifs.organisation.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.AddressResource;

public interface AddressRestService {

    RestResult<AddressResource> findOne(Long id);
}