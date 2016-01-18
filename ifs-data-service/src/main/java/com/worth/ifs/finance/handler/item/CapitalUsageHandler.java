package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.resource.cost.CapitalUsage;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.math.BigDecimal;

public class CapitalUsageHandler extends CostHandler {
    public static final String COST_FIELD_EXISTING = "existing";
    public static final String COST_FIELD_RESIDUAL_VALUE = "residual_value";
    public static final String COST_FIELD_UTILISATION = "utilisation";

    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof CapitalUsage) {
            return mapCapitalUsage(costItem);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        String existing = "";
        BigDecimal residualValue = BigDecimal.ZERO;
        Integer utilisation = 0;

        for(CostValue costValue : cost.getCostValues()) {
            String title = costValue.getCostField().getTitle();
            if(title.equals(COST_FIELD_EXISTING)) {
                existing = costValue.getValue();
            } else if(title.equals(COST_FIELD_RESIDUAL_VALUE)) {
                residualValue = new BigDecimal(costValue.getValue());
            } else if(title.equals(COST_FIELD_UTILISATION)) {
                utilisation = Integer.valueOf(costValue.getValue());
            }
        }

        return new CapitalUsage(cost.getId(), cost.getQuantity(), cost.getDescription(), existing,
                cost.getCost(), residualValue, utilisation);
    }

    public Cost mapCapitalUsage(CostItem costItem) {
        CapitalUsage capitalUsage = (CapitalUsage) costItem;
        Cost capitalUsageCost = new Cost(capitalUsage.getId(), "",  capitalUsage.getDescription(), capitalUsage.getDeprecation(),
                capitalUsage.getNpv(), null, null);
        capitalUsageCost.getCostValues().add(
                new CostValue(capitalUsageCost, costFields.get(COST_FIELD_EXISTING), capitalUsage.getExisting()));
        capitalUsageCost.getCostValues().add(
                new CostValue(capitalUsageCost, costFields.get(COST_FIELD_RESIDUAL_VALUE), String.valueOf(capitalUsage.getResidualValue())));
        capitalUsageCost.getCostValues().add(
                new CostValue(capitalUsageCost, costFields.get(COST_FIELD_UTILISATION), String.valueOf(capitalUsage.getUtilisation())));

        return capitalUsageCost;
    }

}
