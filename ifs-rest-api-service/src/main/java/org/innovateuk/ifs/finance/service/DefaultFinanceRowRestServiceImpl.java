package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.stereotype.Service;

/**
 * FinanceRowRestServiceImpl is a utility for CRUD operations on
 * {@link org.innovateuk.ifs.finance.resource.FinanceRowResource}.
 */
@Service
public class DefaultFinanceRowRestServiceImpl extends BaseFinanceRowRestServiceImpl implements DefaultFinanceRowRestService {

    public DefaultFinanceRowRestServiceImpl() {
        super("/cost");
    }

    @Override
    public RestResult<FinanceRowItem> getCost(long costId) {
        return getWithRestResult(getCostRestUrl() + "/" + costId, FinanceRowItem.class);
    }
}
