package com.worth.ifs.application.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;

import java.util.List;

/**
 * {@code FinanceRowService} retrieving and updating costs
 */
public interface FinanceRowService {
    List<FinanceRowMetaFieldResource> getCostFields();
    RestResult<ValidationMessages> update(FinanceRowItem costItem);
    void delete(Long costId);
    ValidationMessages add(Long applicationFinanceId, Long questionId, FinanceRowItem costItem);
    FinanceRowItem addWithoutPersisting(Long applicationFinanceId, Long questionId);
    FinanceRowItem findById(Long costId);
}
