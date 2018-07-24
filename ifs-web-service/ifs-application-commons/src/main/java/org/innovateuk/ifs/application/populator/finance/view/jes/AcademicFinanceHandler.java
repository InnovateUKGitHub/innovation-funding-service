package org.innovateuk.ifs.application.populator.finance.view.jes;

import org.innovateuk.ifs.application.populator.finance.model.FinanceFormField;
import org.innovateuk.ifs.application.populator.finance.view.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.util.NumberUtils;

import java.math.BigDecimal;
import java.util.List;

public class AcademicFinanceHandler extends FinanceRowHandler {


    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        if(financeFormFields!=null && !financeFormFields.isEmpty()) {
            FinanceFormField academicField = financeFormFields.get(0);
            return toFinanceRowItem(id, academicField);
        }
        return null;
    }

    public FinanceRowItem toFinanceRowItem(Long id, FinanceFormField academicFormField) {
        String key = academicFormField.getCostName();
        BigDecimal value = null;
        String item = null;
        switch(key) {
            case "tsb_reference":
                item = academicFormField.getValue();
                break;
            default:
                value = NumberUtils.getBigDecimalValue(academicFormField.getValue(), 0.0);
        }

        return new AcademicCost(id, key, value, item);
    }
}
