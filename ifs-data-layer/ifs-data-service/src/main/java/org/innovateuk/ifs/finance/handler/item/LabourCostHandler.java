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

import java.math.BigDecimal;
import java.util.Collections;
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
    public static final String THIRDPARTY_OFGEM_NAME_KEY = "third-party-ofgem";

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
                                            labourCostItem.isThirdPartyOfgem() ? labourCostItem.getRate() : labourCostItem.getGrossEmployeeCost(),
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
                    costItem.isThirdPartyOfgem() ? costItem.getRate() : costItem.getGrossEmployeeCost(),
                    null, costItem.getCostType());
    }

    @Override
    public LabourCost toResource(FinanceRow cost) {
        LabourCost labourCost;

        boolean thirdPartyOfgem = cost.getTarget().getCompetition().isThirdPartyOfgem();
        if (THIRDPARTY_OFGEM_NAME_KEY.equals(cost.getName())) {
            labourCost = new LabourCost(cost.getId(), cost.getName(), cost.getItem(), BigDecimal.ONE, cost.getQuantity(),
                    cost.getDescription(), cost.getTarget().getId(), cost.getCost(), thirdPartyOfgem);
        } else {
            labourCost = new LabourCost(cost.getId(), cost.getName(), cost.getItem(), cost.getCost(), cost.getQuantity(),
                    cost.getDescription(), cost.getTarget().getId(), BigDecimal.ZERO, thirdPartyOfgem);
        }

        return labourCost;
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(LABOUR);
    }

    @Override
    protected List<LabourCost> initialiseCosts(Finance finance) {
        String description = LabourCostCategory.WORKING_DAYS_PER_YEAR;
        Integer labourDays = DEFAULT_WORKING_DAYS;
        boolean thirdPartyOfgem = finance.getCompetition().isThirdPartyOfgem();

        List<LabourCost> defaultLabourCost = Collections.emptyList();

        if (!thirdPartyOfgem) {
            defaultLabourCost = newArrayList(new LabourCost(null, LabourCostCategory.WORKING_DAYS_KEY, null,
                    null, labourDays, description, finance.getId(), null, thirdPartyOfgem));
        }

        return defaultLabourCost;
    }
}
