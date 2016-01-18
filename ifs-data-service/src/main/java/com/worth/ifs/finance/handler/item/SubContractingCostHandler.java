package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.SubContractingCost;

public class SubContractingCostHandler extends CostHandler {
    public static final String COST_FIELD_COUNTRY = "country";

    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof SubContractingCost) {
            cost = mapSubContractingCost(costItem);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        String country = "";
        for(CostValue costValue : cost.getCostValues()) {
            if(costValue.getCostField().getTitle().equals(COST_FIELD_COUNTRY)) {
                country = costValue.getValue();
            }
        }

        return new SubContractingCost(cost.getId(), cost.getCost(), country, cost.getItem(), cost.getDescription());
    }

    public Cost mapSubContractingCost(CostItem costItem) {
        SubContractingCost subContractingCost = (SubContractingCost) costItem;
        Cost cost =  new Cost(subContractingCost.getId(), subContractingCost.getName(), subContractingCost.getRole(),
                0, subContractingCost.getCost(),null,null);
        cost.getCostValues().add(
                new CostValue(cost, costFields.get(COST_FIELD_COUNTRY), subContractingCost.getCountry()));
        return cost;
    }
}
