package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;
import org.springframework.stereotype.Service;

@Service
public class FinanceRowMetaValueRestServiceImpl extends BaseRestService implements FinanceRowMetaValueRestService {

    private String restUrl = "/costvalue";

    @Override
    public RestResult<FinanceRowMetaValueResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, FinanceRowMetaValueResource.class);
    }
}
