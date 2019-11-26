package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

/**
 * FinanceRowRestServiceImpl is a utility for CRUD operations on
 * {@link org.innovateuk.ifs.finance.resource.FinanceRowResource}.
 */
public abstract class BaseFinanceRowRestServiceImpl extends BaseRestService implements FinanceRowRestService {

    private String financeRowRestUrl;

    protected BaseFinanceRowRestServiceImpl(String financeRowRestUrl) {
        this.financeRowRestUrl = financeRowRestUrl;
    }

    @Override
    public RestResult<FinanceRowItem> get(long financeRowItemId) {
        return getWithRestResult(financeRowRestUrl + "/" + financeRowItemId,
                FinanceRowItem.class);
    }

    @Override
    public RestResult<FinanceRowItem> create(FinanceRowItem financeRowItem) {
        return postWithRestResult(financeRowRestUrl, financeRowItem,
                FinanceRowItem.class);
    }

    @Override
    public RestResult<ValidationMessages> update(FinanceRowItem financeRowItem) {
        return putWithRestResult(financeRowRestUrl + "/" + financeRowItem.getId(), financeRowItem, ValidationMessages.class);
    }

    @Override
    public RestResult<Void> delete(long financeRowId) {
        return deleteWithRestResult(financeRowRestUrl + "/" + financeRowId);
    }

    protected String getFinanceRowRestUrl() {
        return financeRowRestUrl;
    }
}
