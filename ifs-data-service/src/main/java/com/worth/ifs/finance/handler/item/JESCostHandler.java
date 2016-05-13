package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.validator.AcademicValidator;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;

/***
 *  Handle conversion and validation of the AcademicCost and CostItem objects.
 */
public class JESCostHandler extends CostHandler {

    @Override
    public void validate(@NotNull CostItem costItem, @NotNull BindingResult bindingResult) {
        AcademicCost academicCost = (AcademicCost) costItem;
        super.validate(academicCost, bindingResult);
        AcademicValidator academicValidator = new AcademicValidator();
        academicValidator.validate(academicCost, bindingResult);
    }

    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof AcademicCost) {
            AcademicCost academicCostItem = (AcademicCost) costItem;
            cost = new Cost(academicCostItem.getId(), academicCostItem.getName(), academicCostItem.getItem(), null, null, academicCostItem.getTotal(), null, null);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        return new AcademicCost(cost.getId(), cost.getName(), cost.getCost(), cost.getItem());
    }
}
