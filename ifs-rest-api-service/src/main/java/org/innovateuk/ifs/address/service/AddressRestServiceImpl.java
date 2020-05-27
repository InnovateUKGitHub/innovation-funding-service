package org.innovateuk.ifs.address.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressRestServiceImpl extends BaseRestService implements AddressRestService {
    private static final Log LOG = LogFactory.getLog(AddressRestServiceImpl.class);

    private String addressRestUrl = "/address";

    @Override
    public RestResult<List<AddressResource>> doLookup(String lookup) {
        return getWithRestResultAnonymous(addressRestUrl + "/do-lookup?lookup=" + lookup, ParameterizedTypeReferences.addressResourceListType());
    }

    @Override
    public RestResult<Boolean> validatePostcode(String postcode) {
        LOG.info(addressRestUrl + "/validate-postcode/?postcode=" + postcode);
        return getWithRestResult(addressRestUrl + "/validate-postcode?postcode=" + postcode, Boolean.class);
    }
}
