package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.stereotype.Service;

/**
 * FinanceRowRestServiceImpl is a utility for CRUD operations on
 * {@link org.innovateuk.ifs.finance.resource.FinanceRowResource}.
 */
@Service
public class ApplicationFinanceRowRestServiceImpl extends BaseFinanceRowRestServiceImpl implements ApplicationFinanceRowRestService {

    public ApplicationFinanceRowRestServiceImpl() {
        super("/application-finance-row");
    }

    @Override
    public RestResult<FinanceRowItem> get(long financeRowId) {
        return getWithRestResult(getFinanceRowRestUrl() + "/" + financeRowId, FinanceRowItem.class);
    }
}
