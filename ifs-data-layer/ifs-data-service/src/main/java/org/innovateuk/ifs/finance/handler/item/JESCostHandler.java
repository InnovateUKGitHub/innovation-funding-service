package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.validator.AcademicValidator;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;

/***
 *  Handle conversion and validation of the AcademicCost and FinanceRowItem objects.
 */
public class JESCostHandler extends FinanceRowHandler<AcademicCost> {

    @Override
    public void validate(@NotNull AcademicCost academicCost, @NotNull BindingResult bindingResult) {
        super.validate(academicCost, bindingResult);
        AcademicValidator academicValidator = new AcademicValidator();
        academicValidator.validate(academicCost, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toCost(AcademicCost academicCostItem) {
        return academicCostItem != null ?
                new ApplicationFinanceRow(academicCostItem.getId(), academicCostItem.getName(), academicCostItem.getItem(),null, null, academicCostItem.getTotal(), null, null) : null;
    }

    @Override
    public ProjectFinanceRow toProjectCost(AcademicCost costItem) {
        return new ProjectFinanceRow(costItem.getId(), costItem.getName(), costItem.getItem(), null, null, costItem.getTotal(), null, null);
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public FinanceRowItem toCostItem(ProjectFinanceRow cost) {
        return buildRowItem(cost);
    }

    private FinanceRowItem buildRowItem(FinanceRow cost){
        return new AcademicCost(cost.getId(), cost.getName(), cost.getCost(), cost.getItem());
    }
}
