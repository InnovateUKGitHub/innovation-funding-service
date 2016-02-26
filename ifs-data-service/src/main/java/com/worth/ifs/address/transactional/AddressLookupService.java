package com.worth.ifs.address.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.security.NotSecured;

import java.util.List;

/**
 * Lookup for addresses
 */
public interface AddressLookupService {
    @NotSecured("Everyone may do a lookup it is used as part of the registration process")
    ServiceResult<List<AddressResource>> doLookup(String lookup);
}
