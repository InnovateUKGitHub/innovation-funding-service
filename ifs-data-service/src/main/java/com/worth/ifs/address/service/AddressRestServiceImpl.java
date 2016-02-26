package com.worth.ifs.address.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.ParameterizedTypeReferences;
import com.worth.ifs.address.resource.AddressResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressRestServiceImpl extends BaseRestService implements AddressRestService {
    @Value("${ifs.data.service.rest.address}")
    String addressRestUrl;

    @Override
    public RestResult<AddressResource> findOne(Long id) {
        return getWithRestResult(addressRestUrl + "/" + id, AddressResource.class);
    }

    @Override
    public RestResult<List<AddressResource>> doLookup(String lookup) {
        return getWithRestResult(addressRestUrl + "/doLookup/" + lookup, ParameterizedTypeReferences.addressResourceListType());
    }
}