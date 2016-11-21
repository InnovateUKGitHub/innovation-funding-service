package com.worth.ifs.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;

import java.util.List;

/**
 * Interface for CRUD operations on {@link FinanceRowItem} related data.
 */
public interface FinanceRowRestService {
    RestResult<ValidationMessages> add(Long applicationFinanceId, Long questionId, FinanceRowItem costItem);
    RestResult<FinanceRowItem> addWithoutPersisting(Long applicationFinanceId, Long questionId);
    RestResult<List<FinanceRowItem>> getCosts(Long applicationFinanceId);
    RestResult<ValidationMessages> update(FinanceRowItem costItem);
    RestResult<FinanceRowItem> findById(Long id);
    RestResult<Void> delete(Long costId);
}
