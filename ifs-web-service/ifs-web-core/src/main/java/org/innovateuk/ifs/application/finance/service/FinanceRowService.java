package org.innovateuk.ifs.application.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

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
