package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

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
