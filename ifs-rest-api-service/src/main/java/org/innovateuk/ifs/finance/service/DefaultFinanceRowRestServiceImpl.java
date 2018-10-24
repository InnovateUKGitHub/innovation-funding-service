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
    public RestResult<Void> delete(long costId) {
        return deleteWithRestResult(getCostRestUrl() + "/delete/" + costId);
    }

    @Override
    public RestResult<FinanceRowItem> addWithResponse(long applicationFinanceId, FinanceRowItem costItem) {
        return postWithRestResult(getCostRestUrl() + "/add-with-response/" + applicationFinanceId, costItem,
                FinanceRowItem.class);
    }

    @Override
    public RestResult<FinanceRowItem> getCost(long costId) {
        return getWithRestResult(getCostRestUrl() + "/" + costId, FinanceRowItem.class);
    }
}
