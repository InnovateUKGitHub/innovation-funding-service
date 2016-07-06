package com.worth.ifs.application.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.util.List;

/**
 * {@code CostService} retrieving and updating costs
 */
public interface CostService {
    List<CostFieldResource> getCostFields();
    RestResult<ValidationMessages> update(CostItem costItem);
    void delete(Long costId);
    CostItem add(Long applicationFinanceId, Long questionId, CostItem costItem);
    CostItem addWithoutPersisting(Long applicationFinanceId, Long questionId);
}
