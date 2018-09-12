package org.innovateuk.ifs.address.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

public interface AddressRestService {
    RestResult<List<AddressResource>> doLookup(String lookup);
    RestResult<Boolean> validatePostcode(String postcode);
    RestResult<AddressResource> getById(Long id);
}
