package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.finance.service.FinanceRowRestService;

/**
 * Base methods for all FinanceFormHandlers. For example methods that handle exceptions or errors that are possibly occurring in all FinanceFormHandlers.
 */
public abstract class BaseFinanceFormHandler<FinanceRowRestServiceType extends FinanceRowRestService> {

    private final FinanceRowRestServiceType financeRowRestService;

    protected BaseFinanceFormHandler(final FinanceRowRestServiceType financeRowRestService) {
        this.financeRowRestService = financeRowRestService;
    }

    protected FinanceRowRestServiceType getFinanceRowRestService() {
        return financeRowRestService;
    }
}
