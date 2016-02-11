package com.worth.ifs.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.resource.CostValueResource;

public interface CostValueRestService {

    RestResult<CostValueResource> findOne(Long id);
}