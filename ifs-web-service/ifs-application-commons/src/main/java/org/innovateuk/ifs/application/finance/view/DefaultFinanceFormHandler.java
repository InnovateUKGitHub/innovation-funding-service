package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.lang3.NotImplementedException;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

/**
 * {@code DefaultFinanceFormHandler} retrieves the costs and handles the finance data retrieved from the request, so it can be
 * transfered to view or stored. The costs retrieved from the {@link FinanceRowRestService} are converted
 * to {@link FinanceRowItem}.
 */
@Component
public class DefaultFinanceFormHandler extends BaseFinanceFormHandler<ApplicationFinanceRowRestService> implements FinanceFormHandler {

    private final FinanceService financeService;

    @Autowired
    public DefaultFinanceFormHandler(final FinanceService financeService,
                                     final ApplicationFinanceRowRestService defaultFinanceRowRestService) {
        super(defaultFinanceRowRestService);
        this.financeService = financeService;
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long applicationFinanceId) {
        throw new NotImplementedException("Finance upload is not available for the default finances");
    }
}