package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

/**
 * Interface for CRUD operations on {@link FinanceRowItem} related data.
 */
public interface FinanceRowRestService {

    RestResult<FinanceRowItem> create(FinanceRowItem financeRowItem);

    RestResult<ValidationMessages> update(FinanceRowItem financeRowItem);

    RestResult<Void> delete(long financeRowId);

    RestResult<FinanceRowItem> get(long financeRowId);

}
