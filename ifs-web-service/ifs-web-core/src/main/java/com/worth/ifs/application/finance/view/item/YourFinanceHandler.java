package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.util.List;

/**
 * Handles the conversion of form fields to your finance item
 */
public class YourFinanceHandler extends FinanceRowHandler {
    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {
        return null;
    }
}
