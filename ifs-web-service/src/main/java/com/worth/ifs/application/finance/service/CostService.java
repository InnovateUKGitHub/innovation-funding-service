package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.util.List;

/**
 * {@code CostService} retrieving and updating costs
 */
public interface CostService {
    CostItem getById(Long costId);
    List<CostFieldResource> getCostFields();
    public void update(CostItem costItem);
    public void delete(Long costId);
    public CostItem add(Long applicationFinanceId, Long questionId, CostItem costItem);
}
