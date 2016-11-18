package com.worth.ifs.address.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.ParameterizedTypeReferences;
import com.worth.ifs.address.resource.AddressResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressRestServiceImpl extends BaseRestService implements AddressRestService {
    private static final Log LOG = LogFactory.getLog(AddressRestServiceImpl.class);

    private String addressRestUrl = "/address";

    @Override
    public RestResult<List<AddressResource>> doLookup(String lookup) {
        return getWithRestResult(addressRestUrl + "/doLookup?lookup=" + lookup, ParameterizedTypeReferences.addressResourceListType());
    }

    @Override
    public RestResult<Boolean> validatePostcode(String postcode) {
        LOG.info(addressRestUrl + "/validatePostcode/?postcode=" + postcode);
        return getWithRestResult(addressRestUrl + "/validatePostcode?postcode=" + postcode, Boolean.class);
    }

    @Override
    public RestResult<AddressResource> getById(Long id) {
        return getWithRestResult(addressRestUrl + "/" + id, AddressResource.class);
    }
}