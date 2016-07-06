package com.worth.ifs.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.util.List;

/**
 * Interface for CRUD operations on {@link CostItem} related data.
 */
public interface CostRestService{
    RestResult<CostItem> add(Long applicationFinanceId, Long questionId, CostItem costItem);
    RestResult<CostItem> addWithoutPersisting(Long applicationFinanceId, Long questionId);
    RestResult<List<CostItem>> getCosts(Long applicationFinanceId);
    RestResult<ValidationMessages> update(CostItem costItem);
    RestResult<CostItem> findById(Long id);
    RestResult<Void> delete(Long costId);
}
