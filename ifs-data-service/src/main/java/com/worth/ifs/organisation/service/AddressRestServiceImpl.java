package com.worth.ifs.organisation.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.organisation.resource.AddressResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AddressRestServiceImpl extends BaseRestService implements AddressRestService {

    @Value("${ifs.data.service.rest.address}")
    private String restUrl;

    @Override
    public RestResult<AddressResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, AddressResource.class);
    }
}