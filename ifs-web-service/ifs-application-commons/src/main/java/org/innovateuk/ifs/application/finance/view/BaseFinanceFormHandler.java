package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;

/**
 * Base methods for all FinanceFormHandlers. For example methods that handle exceptions or errors that are possibly occurring in all FinanceFormHandlers.
 */
public abstract class BaseFinanceFormHandler<FinanceRowRestServiceType extends FinanceRowRestService> {

    private final FinanceRowRestServiceType financeRowRestService;
    private final UnsavedFieldsManager unsavedFieldsManager;

    private static final String UNSPECIFIED_AMOUNT_STR = "£ 0";

    private static final Log LOG = LogFactory.getLog(BaseFinanceFormHandler.class);

    protected BaseFinanceFormHandler(final FinanceRowRestServiceType financeRowRestService,
                                     final UnsavedFieldsManager unsavedFieldsManager) {
        this.financeRowRestService = financeRowRestService;
        this.unsavedFieldsManager = unsavedFieldsManager;
    }

    protected FinanceRowRestServiceType getFinanceRowRestService() {
        return financeRowRestService;
    }
}
