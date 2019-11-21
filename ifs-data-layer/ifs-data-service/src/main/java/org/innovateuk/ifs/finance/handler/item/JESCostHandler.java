package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.validator.AcademicValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/***
 *  Handle conversion and validation of the AcademicCost and FinanceRowItem objects.
 */
@Component
public class JESCostHandler extends FinanceRowHandler<AcademicCost> {

    @Override
    public void validate(@NotNull AcademicCost academicCost, @NotNull BindingResult bindingResult) {
        super.validate(academicCost, bindingResult);
        AcademicValidator academicValidator = new AcademicValidator();
        academicValidator.validate(academicCost, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(AcademicCost academicCostItem) {
        return academicCostItem != null ?
                new ApplicationFinanceRow(academicCostItem.getId(), academicCostItem.getName(), academicCostItem.getItem(), academicCostItem.getCostType().name(), null, academicCostItem.getTotal(), null, academicCostItem.getCostType()) : null;
    }

    @Override
    public ProjectFinanceRow toProjectDomain(AcademicCost costItem) {
        return new ProjectFinanceRow(costItem.getId(), costItem.getName(), costItem.getItem(), costItem.getCostType().name(), null, costItem.getTotal(), null, costItem.getCostType());
    }

    @Override
    public AcademicCost toResource(FinanceRow cost) {
        return new AcademicCost(cost.getId(), cost.getName(), cost.getCost(), cost.getItem(), FinanceRowType.valueOf(cost.getDescription()), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.empty();
    }
}
