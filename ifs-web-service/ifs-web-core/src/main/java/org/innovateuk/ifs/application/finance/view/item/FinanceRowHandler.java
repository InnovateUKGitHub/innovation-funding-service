package org.innovateuk.ifs.application.finance.view.item;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CostHandlers are used to convert form fields to costItems
 */
public abstract class FinanceRowHandler {
    protected Log LOG = LogFactory.getLog(this.getClass());
    Map<String, FinanceRowMetaFieldResource> costFields = new HashMap<>();

    public abstract FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields);

    public FinanceRowHandler() {
    }

}
