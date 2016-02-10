package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.security.NotSecured;

import java.util.List;

public interface CostFieldService {
    @NotSecured("TODO")
    ServiceResult<CostField> findOne(Long id);
    @NotSecured("TODO")
    ServiceResult<List<CostFieldResource>> findAll();
}