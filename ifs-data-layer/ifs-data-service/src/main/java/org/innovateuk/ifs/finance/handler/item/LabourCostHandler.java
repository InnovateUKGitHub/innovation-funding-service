package org.innovateuk.ifs.finance.handler.item;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.LABOUR;

/**
 * Handles the labour costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class LabourCostHandler extends FinanceRowHandler<LabourCost> {
    public static final String COST_KEY = "labour";
    public static final Integer DEFAULT_WORKING_DAYS = 232;

    @Override
    public void validate(LabourCost labourCost, BindingResult bindingResult) {
        if (StringUtils.isNotEmpty(labourCost.getName()) && (labourCost.getName().equals(LabourCostCategory.WORKING_DAYS_KEY) || labourCost.getName().equals(LabourCostCategory.WORKING_DAYS_PER_YEAR))) {
            super.validate(labourCost, bindingResult, LabourCost.YearlyWorkingDays.class);
        } else {
            super.validate(labourCost, bindingResult);
        }
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(LabourCost labourCostItem) {
        return labourCostItem != null ? new ApplicationFinanceRow(
                                            labourCostItem.getId(),
                                            labourCostItem.getName(),
                                            labourCostItem.getRole(),
                                            labourCostItem.getDescription(),
                                            labourCostItem.getLabourDays(),
                                            labourCostItem.getGrossEmployeeCost(),
                                            null, labourCostItem.getCostType()) : null;
    }

    @Override
    public ProjectFinanceRow toProjectDomain(LabourCost costItem) {
        return new ProjectFinanceRow(
                    costItem.getId(),
                    costItem.getName(),
                    costItem.getRole(),
                    costItem.getDescription(),
                    costItem.getLabourDays(),
                    costItem.getGrossEmployeeCost(),
                    null, costItem.getCostType());
    }

    @Override
    public LabourCost toResource(FinanceRow cost) {
        return new LabourCost(cost.getId(), cost.getName(), cost.getItem(), cost.getCost(), cost.getQuantity(), cost.getDescription(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(LABOUR);
    }

    @Override
    protected List<LabourCost> intialiseCosts(Finance finance) {
        String description = LabourCostCategory.WORKING_DAYS_PER_YEAR;
        Integer labourDays = DEFAULT_WORKING_DAYS;
        return newArrayList(new LabourCost(null, LabourCostCategory.WORKING_DAYS_KEY, null, null, labourDays, description, finance.getId()));
    }

}
