package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

/**
 * Interface for CRUD operations on {@link FinanceRowItem} related data.
 */
public interface ApplicationFinanceRowRestService extends FinanceRowRestService {

    RestResult<FinanceRowItem> get(long financeRowId);

}
