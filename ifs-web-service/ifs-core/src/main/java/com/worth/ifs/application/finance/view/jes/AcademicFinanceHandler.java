package com.worth.ifs.application.finance.view.jes;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.view.item.CostHandler;
import com.worth.ifs.application.finance.view.item.NumberUtils;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.math.BigDecimal;
import java.util.List;

public class AcademicFinanceHandler extends CostHandler {


    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {
        if(financeFormFields!=null && !financeFormFields.isEmpty()) {
            FinanceFormField academicField = financeFormFields.get(0);
            return toCostItem(id, academicField);
        }
        return null;
    }

    public CostItem toCostItem(Long id, FinanceFormField academicFormField) {
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
