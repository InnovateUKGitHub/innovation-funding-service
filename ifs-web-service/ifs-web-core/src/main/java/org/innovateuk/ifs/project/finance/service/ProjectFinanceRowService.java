package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

/**
 * Web layer service to support project finance related operations.
 */
public interface ProjectFinanceRowService {
    ValidationMessages add(Long projectFinanceId, Long questionId, FinanceRowItem costItem);
    RestResult<ValidationMessages> update(FinanceRowItem costItem);
    void delete(Long costId);
    FinanceRowItem addWithoutPersisting(Long projectFinanceId, Long questionId);
    FinanceRowItem findById(Long costId);
}
