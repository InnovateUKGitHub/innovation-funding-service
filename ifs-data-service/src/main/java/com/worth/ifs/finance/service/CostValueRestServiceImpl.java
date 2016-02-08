package com.worth.ifs.finance.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.resource.CostValueResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CostValueRestServiceImpl extends BaseRestService implements CostValueRestService {
    @Value("${ifs.data.service.rest.costvalue}")
    private String restUrl;


    @Override
    public CostValueResource findOne(Long id) {
        return restGet(restUrl + "/" + id, CostValueResource.class);
    }
}