package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CostHandlers are used to convert form fields to costItems
 */
public abstract class CostHandler {
    protected Log LOG = LogFactory.getLog(this.getClass());
    Map<String, CostFieldResource> costFields = new HashMap<>();

    public abstract CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields);

    public CostHandler() {
    }

}
