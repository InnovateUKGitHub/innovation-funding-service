package com.worth.ifs.application.finance.view.jes;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.view.item.CostHandler;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.math.BigDecimal;
import java.util.List;

public class AcademicFinanceHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {

        return new AcademicCost(id, BigDecimal.ZERO, "");
    }
}
