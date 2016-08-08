package com.worth.ifs.application.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.util.List;

/**
 * {@code FinanceRowService} retrieving and updating costs
 */
public interface FinanceRowService {
    List<FinanceRowMetaFieldResource> getCostFields();
    RestResult<ValidationMessages> update(CostItem costItem);
    void delete(Long costId);
    ValidationMessages add(Long applicationFinanceId, Long questionId, CostItem costItem);
    CostItem addWithoutPersisting(Long applicationFinanceId, Long questionId);
    CostItem findById(Long costId);
}
