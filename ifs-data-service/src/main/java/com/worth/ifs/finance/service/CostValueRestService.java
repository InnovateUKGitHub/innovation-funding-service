package com.worth.ifs.finance.service;

import com.worth.ifs.finance.resource.CostValueResource;

public interface CostValueRestService {

    CostValueResource findOne(Long id);
}