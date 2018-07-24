package org.innovateuk.ifs.application.populator.finance.view.item;

import org.innovateuk.ifs.application.populator.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

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
