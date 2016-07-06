package com.worth.ifs.application.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    AddressRestService addressRestService;

    @Override
    public ServiceResult<AddressResource> getById(long id) {
        return addressRestService.getById(id).toServiceResult();
    }
}
