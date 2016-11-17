package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.domain.FinanceRowMetaValue;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.SubContractingCost;

/**
 * Handles the subcontracting costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class SubContractingCostHandler extends FinanceRowHandler {
    public static final String COST_FIELD_COUNTRY = "country";
    public static final String COST_KEY = "subcontracting";

    @Override
    public ApplicationFinanceRow toCost(FinanceRowItem costItem) {
        ApplicationFinanceRow cost = null;
        if (costItem instanceof SubContractingCost) {
            cost = mapSubContractingCost(costItem);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        String country = "";
        for(FinanceRowMetaValue costValue : ((ApplicationFinanceRow)cost).getCostValues()) {
            if(costValue.getFinanceRowMetaField() != null && costValue.getFinanceRowMetaField().getTitle().equals(COST_FIELD_COUNTRY)) {
                country = costValue.getValue();
            }
        }

        return new SubContractingCost(cost.getId(), cost.getCost(), country, cost.getItem(), cost.getDescription());
    }

    public ApplicationFinanceRow mapSubContractingCost(FinanceRowItem costItem) {
        SubContractingCost subContractingCost = (SubContractingCost) costItem;
        ApplicationFinanceRow cost =  new ApplicationFinanceRow(subContractingCost.getId(), COST_KEY, subContractingCost.getName(), subContractingCost.getRole(),
                0, subContractingCost.getCost(),null,null);
        cost.addCostValues(
                new FinanceRowMetaValue(cost, costFields.get(COST_FIELD_COUNTRY), subContractingCost.getCountry()));
        return cost;
    }
}
