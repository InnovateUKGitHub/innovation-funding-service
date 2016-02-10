package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.security.NotSecured;

import java.util.List;

public interface CostService {

    @NotSecured("TODO")
    ServiceResult<CostField> findOne(Long id);

    @NotSecured("TODO")
    ServiceResult<List<CostFieldResource>> findAll();

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<CostItem> add(Long applicationFinanceId, Long questionId, CostItem newCostItem);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> update(Long id, CostItem newCostItem);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<List<Cost>> findByApplicationId(Long applicationFinanceId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Cost> findById(Long id);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> delete(Long costId);
}