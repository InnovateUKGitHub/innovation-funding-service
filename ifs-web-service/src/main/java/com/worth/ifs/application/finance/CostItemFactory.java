package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.*;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostValue;

/**
 * Creates specific costs for each category and maps the cost to cost items, which
 * can be used in the view.
 */
public class CostItemFactory {
    public static final String COST_FIELD_EXISTING = "existing";
    public static final String COST_FIELD_RESIDUAL_VALUE = "residual_value";
    public static final String COST_FIELD_UTILISATION = "utilisation";
    public static final String COST_FIELD_COUNTRY = "country";

    public CostItem createCostItem(CostType costType, Cost cost) {
        switch(costType) {
            case LABOUR:
                return new LabourCost(cost.getId(), cost.getItem(), cost.getCost(), cost.getQuantity(), cost.getDescription());
            case CAPITAL_USAGE:
                return createCapitalUsage(cost);
            case MATERIALS:
                return new Materials(cost.getId(),cost.getItem(),cost.getCost(),cost.getQuantity());
            case OTHER_COSTS:
                return new OtherCost(cost.getId(), cost.getCost(),cost.getDescription());
            case OVERHEADS:
                return new Overhead(cost.getItem(), cost.getQuantity());
            case SUBCONTRACTING_COSTS:
                return createSubcontractingCost(cost);
            case TRAVEL:
                return new TravelCost(cost.getId(), cost.getCost(), cost.getItem(), cost.getQuantity());
        }
        return null;
    }

    private CostItem createCapitalUsage(Cost cost) {
        String existing = "";
        Double residualValue = 0d;
        Integer utilisation = 0;

        for(CostValue costValue : cost.getCostValues()) {
            if(costValue.getCostField().getTitle().equals(COST_FIELD_EXISTING)) {
                existing = costValue.getValue();
            } else if(costValue.getCostField().getTitle().equals(COST_FIELD_RESIDUAL_VALUE)) {
                residualValue = Double.valueOf(costValue.getValue());
            } else if(costValue.getCostField().getTitle().equals(COST_FIELD_UTILISATION)) {
                utilisation = Integer.valueOf(costValue.getValue());
            }
        }

        return new CapitalUsage(cost.getId(), cost.getQuantity(), cost.getDescription(), existing,
                cost.getCost(), residualValue, utilisation);

    }

    private CostItem createSubcontractingCost(Cost cost) {
        String country = "";
        for(CostValue costValue : cost.getCostValues()) {
            if(costValue.getCostField().getTitle().equals(COST_FIELD_COUNTRY)) {
                country = costValue.getValue();
            }
        }

        return new SubContractingCost(cost.getId(), cost.getCost(), country, cost.getItem(), cost.getDescription());
    }

}
