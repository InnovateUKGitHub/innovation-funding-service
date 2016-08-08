package com.worth.ifs.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.resource.FinanceRowMetaValueResource;

public interface FinanceRowMetaValueRestService {

    RestResult<FinanceRowMetaValueResource> findOne(Long id);
}