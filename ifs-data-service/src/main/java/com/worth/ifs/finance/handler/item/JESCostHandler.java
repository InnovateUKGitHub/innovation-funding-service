package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.validator.AcademicValidator;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;

/***
 *  Handle conversion and validation of the AcademicCost and FinanceRowItem objects.
 */
public class JESCostHandler extends FinanceRowHandler {

    @Override
    public void validate(@NotNull FinanceRowItem costItem, @NotNull BindingResult bindingResult) {
        AcademicCost academicCost = (AcademicCost) costItem;
        super.validate(academicCost, bindingResult);
        AcademicValidator academicValidator = new AcademicValidator();
        academicValidator.validate(academicCost, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toCost(FinanceRowItem costItem) {
        ApplicationFinanceRow cost = null;
        if (costItem instanceof AcademicCost) {
            AcademicCost academicCostItem = (AcademicCost) costItem;
            cost = new ApplicationFinanceRow(academicCostItem.getId(), academicCostItem.getName(), academicCostItem.getItem(), null, null, academicCostItem.getTotal(), null, null);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return new AcademicCost(cost.getId(), cost.getName(), cost.getCost(), cost.getItem());
    }
}
