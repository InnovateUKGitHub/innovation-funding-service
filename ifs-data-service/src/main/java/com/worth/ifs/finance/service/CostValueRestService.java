package com.worth.ifs.finance.service;

import com.worth.ifs.finance.resource.CostValueResource;
import com.worth.ifs.security.NotSecured;

public interface CostValueRestService {
    @NotSecured("REST Service")
    CostValueResource findOne(Long id);
}