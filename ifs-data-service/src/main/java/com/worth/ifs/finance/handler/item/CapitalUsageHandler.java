package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.domain.FinanceRowMetaValue;
import com.worth.ifs.finance.resource.cost.CapitalUsage;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;

import java.math.BigDecimal;

/**
 * Handles the capital usage costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class CapitalUsageHandler extends FinanceRowHandler {
    public static final String COST_FIELD_EXISTING = "existing";
    public static final String COST_FIELD_RESIDUAL_VALUE = "residual_value";
    public static final String COST_FIELD_UTILISATION = "utilisation";
    public static final String COST_KEY = "capital-usage";


    @Override
    public FinanceRow toCost(FinanceRowItem costItem) {
        FinanceRow cost = null;
        if (costItem instanceof CapitalUsage) {
            return mapCapitalUsage(costItem);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(FinanceRow cost) {
        String existing = "";
        BigDecimal residualValue = BigDecimal.ZERO;
        Integer utilisation = 0;

        for (FinanceRowMetaValue costValue : cost.getCostValues()) {
            if(costValue.getFinanceRowMetaField() != null && costValue.getFinanceRowMetaField().getTitle() != null){
                String title = costValue.getFinanceRowMetaField().getTitle();
                if (title.equals(COST_FIELD_EXISTING)) {
                    existing = costValue.getValue();
                } else if (title.equals(COST_FIELD_RESIDUAL_VALUE)) {
                    residualValue = new BigDecimal(costValue.getValue());
                } else if (title.equals(COST_FIELD_UTILISATION)) {
                    utilisation = Integer.valueOf(costValue.getValue());
                }
            }
        }

        return new CapitalUsage(cost.getId(), cost.getQuantity(), cost.getDescription(), existing,
                cost.getCost(), residualValue, utilisation);
    }

    public FinanceRow mapCapitalUsage(FinanceRowItem costItem) {
        CapitalUsage capitalUsage = (CapitalUsage) costItem;
        FinanceRow capitalUsageCost = new FinanceRow(capitalUsage.getId(), COST_KEY, "", capitalUsage.getDescription(), capitalUsage.getDeprecation(),
                capitalUsage.getNpv(), null, null);
        capitalUsageCost.addCostValues(
                new FinanceRowMetaValue(capitalUsageCost, costFields.get(COST_FIELD_EXISTING), capitalUsage.getExisting()),
                new FinanceRowMetaValue(capitalUsageCost, costFields.get(COST_FIELD_RESIDUAL_VALUE), String.valueOf(capitalUsage.getResidualValue())),
                new FinanceRowMetaValue(capitalUsageCost, costFields.get(COST_FIELD_UTILISATION), String.valueOf(capitalUsage.getUtilisation())));

        return capitalUsageCost;
    }
}
