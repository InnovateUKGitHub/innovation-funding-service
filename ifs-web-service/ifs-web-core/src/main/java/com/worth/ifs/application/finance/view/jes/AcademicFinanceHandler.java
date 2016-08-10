package com.worth.ifs.application.finance.view.jes;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.view.item.FinanceRowHandler;
import com.worth.ifs.util.NumberUtils;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;

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
