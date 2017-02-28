package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.SubContractingCost;

import java.util.List;

/**
 * Handles the subcontracting costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class SubContractingCostHandler extends FinanceRowHandler<SubContractingCost> {
    public static final String COST_FIELD_COUNTRY = "country";
    public static final String COST_KEY = "subcontracting";

    @Override
    public ApplicationFinanceRow toCost(SubContractingCost costItem) {
        return costItem != null ? mapSubContractingCost(costItem) : null;
    }

    @Override
    public ProjectFinanceRow toProjectCost(SubContractingCost costItem) {
        return mapSubContractingToProjectCost(costItem);
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return buildRowItem(cost, cost.getFinanceRowMetadata());
    }

    @Override
    public FinanceRowItem toCostItem(ProjectFinanceRow cost) {
        return buildRowItem(cost, cost.getFinanceRowMetadata());
    }

    private FinanceRowItem buildRowItem(FinanceRow cost, List<FinanceRowMetaValue> financeRowMetaValues){
        String country = "";
        for(FinanceRowMetaValue costValue : financeRowMetaValues) {
            if(costValue.getFinanceRowMetaField() != null && costValue.getFinanceRowMetaField().getTitle().equals(COST_FIELD_COUNTRY)) {
                country = costValue.getValue();
            }
        }

        return new SubContractingCost(cost.getId(), cost.getCost(), country, cost.getItem(), cost.getDescription());
    }

    private ApplicationFinanceRow mapSubContractingCost(FinanceRowItem costItem) {
        SubContractingCost subContractingCost = (SubContractingCost) costItem;
        ApplicationFinanceRow cost =  new ApplicationFinanceRow(subContractingCost.getId(), COST_KEY, subContractingCost.getName(), subContractingCost.getRole(),
                0, subContractingCost.getCost(),null,null);
        cost.addCostValues(
                new FinanceRowMetaValue(cost, costFields.get(COST_FIELD_COUNTRY), subContractingCost.getCountry()));
        return cost;
    }

    private ProjectFinanceRow mapSubContractingToProjectCost(FinanceRowItem costItem) {
        SubContractingCost subContractingCost = (SubContractingCost) costItem;
        ProjectFinanceRow cost =  new ProjectFinanceRow(subContractingCost.getId(), COST_KEY, subContractingCost.getName(), subContractingCost.getRole(),
                0, subContractingCost.getCost(),null,null);
        cost.addCostValues(
                new FinanceRowMetaValue(cost, costFields.get(COST_FIELD_COUNTRY), subContractingCost.getCountry()));
        return cost;
    }
}
