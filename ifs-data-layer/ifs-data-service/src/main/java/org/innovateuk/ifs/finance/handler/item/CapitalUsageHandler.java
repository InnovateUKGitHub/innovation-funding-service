package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.CapitalUsage;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.CAPITAL_USAGE;

/**
 * Handles the capital usage costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class CapitalUsageHandler extends FinanceRowHandler<CapitalUsage> {
    public static final String COST_FIELD_EXISTING = "existing";
    public static final String COST_FIELD_RESIDUAL_VALUE = "residual_value";
    public static final String COST_FIELD_UTILISATION = "utilisation";
    public static final String COST_KEY = "capital-usage";

    @Override
    public ApplicationFinanceRow toApplicationDomain(CapitalUsage costItem) {
        return costItem != null ? mapCapitalUsage(costItem) : null;
    }

    @Override
    public ProjectFinanceRow toProjectDomain(CapitalUsage costItem) {
        return costItem != null ? mapCapitalUsageToProjectCost(costItem) : null;
    }

    @Override
    public CapitalUsage toResource(FinanceRow cost) {
        return buildRowItem(cost, cost.getFinanceRowMetadata());
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return CAPITAL_USAGE;
    }

    private CapitalUsage buildRowItem(FinanceRow cost, List<FinanceRowMetaValue> financeRowMetaValues){
        String existing = "";
        BigDecimal residualValue = BigDecimal.ZERO;
        Integer utilisation = 0;

        for (FinanceRowMetaValue costValue : financeRowMetaValues) {
            if(costValue.getFinanceRowMetaField() != null && costValue.getFinanceRowMetaField().getTitle() != null){
                String title = costValue.getFinanceRowMetaField().getTitle();
                if (title.equals(COST_FIELD_EXISTING)) {
                    existing = "null".equals(costValue.getValue()) ? null : costValue.getValue();
                } else if (title.equals(COST_FIELD_RESIDUAL_VALUE)) {
                    residualValue = "null".equals(costValue.getValue()) ? null : new BigDecimal(costValue.getValue());
                } else if (title.equals(COST_FIELD_UTILISATION)) {
                    utilisation = "null".equals(costValue.getValue()) ? null : Integer.valueOf(costValue.getValue());
                }
            }
        }

        return new CapitalUsage(cost.getId(), cost.getQuantity(), cost.getDescription(), existing, cost.getCost(), residualValue, utilisation, cost.getTarget().getId());
    }

    private ApplicationFinanceRow mapCapitalUsage(FinanceRowItem costItem) {
        CapitalUsage capitalUsage = (CapitalUsage) costItem;
        ApplicationFinanceRow capitalUsageCost = new ApplicationFinanceRow(capitalUsage.getId(), COST_KEY, "", capitalUsage.getDescription(), capitalUsage.getDeprecation(),
                capitalUsage.getNpv(), null, costItem.getCostType());
        capitalUsageCost.addCostValues(
                new FinanceRowMetaValue(capitalUsageCost, costFields.get(COST_FIELD_EXISTING), capitalUsage.getExisting()),
                new FinanceRowMetaValue(capitalUsageCost, costFields.get(COST_FIELD_RESIDUAL_VALUE), String.valueOf(capitalUsage.getResidualValue())),
                new FinanceRowMetaValue(capitalUsageCost, costFields.get(COST_FIELD_UTILISATION), String.valueOf(capitalUsage.getUtilisation())));

        return capitalUsageCost;
    }

    private ProjectFinanceRow mapCapitalUsageToProjectCost(CapitalUsage capitalUsage) {
        ProjectFinanceRow capitalUsageCost = new ProjectFinanceRow(capitalUsage.getId(), COST_KEY, "", capitalUsage.getDescription(), capitalUsage.getDeprecation(),
                capitalUsage.getNpv(), null, capitalUsage.getCostType());
        capitalUsageCost.addCostValues(
                new FinanceRowMetaValue(capitalUsageCost, costFields.get(COST_FIELD_EXISTING), capitalUsage.getExisting()),
                new FinanceRowMetaValue(capitalUsageCost, costFields.get(COST_FIELD_RESIDUAL_VALUE), String.valueOf(capitalUsage.getResidualValue())),
                new FinanceRowMetaValue(capitalUsageCost, costFields.get(COST_FIELD_UTILISATION), String.valueOf(capitalUsage.getUtilisation())));

        return capitalUsageCost;
    }
}
