package com.worth.ifs.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.resource.FinanceRowMetaValueResource;
import org.springframework.stereotype.Service;

@Service
public class FinanceRowMetaValueRestServiceImpl extends BaseRestService implements FinanceRowMetaValueRestService {

    private String restUrl = "/costvalue";

    @Override
    public RestResult<FinanceRowMetaValueResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, FinanceRowMetaValueResource.class);
    }
}