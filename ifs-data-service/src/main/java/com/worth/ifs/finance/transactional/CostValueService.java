package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.CostValueId;
import com.worth.ifs.finance.resource.CostValueResource;
import com.worth.ifs.security.NotSecured;

public interface CostValueService {

    @NotSecured("TODO")
    ServiceResult<CostValueResource> findOne(CostValueId id);
}