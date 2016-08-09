package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;

import java.util.List;

/**
 * Handles the conversion of form fields to your finance item
 */
public class YourFinanceHandler extends FinanceRowHandler {
    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        return null;
    }
}
