package org.innovateuk.ifs.address.service;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressRestServiceImpl extends BaseRestService implements AddressRestService {

    private String addressRestUrl = "/address";

    @Override
    public RestResult<List<AddressResource>> doLookup(String lookup) {
        return getWithRestResultAnonymous(addressRestUrl + "/do-lookup?lookup=" + lookup, ParameterizedTypeReferences.addressResourceListType());
    }

    @Override
    public RestResult<Boolean> validatePostcode(String postcode) {
        log.info(addressRestUrl + "/validate-postcode/?postcode=" + postcode);
        return getWithRestResult(addressRestUrl + "/validate-postcode?postcode=" + postcode, Boolean.class);
    }
}
