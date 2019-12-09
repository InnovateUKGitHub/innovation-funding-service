package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.SubContractingCost;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.SUBCONTRACTING_COSTS;

/**
 * Handles the subcontracting costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class SubContractingCostHandler extends FinanceRowHandler<SubContractingCost> {
    public static final String COST_FIELD_COUNTRY = "country";
    public static final String COST_KEY = "subcontracting";

    @Override
    public ApplicationFinanceRow toApplicationDomain(SubContractingCost costItem) {
        return costItem != null ? mapSubContractingCost(costItem) : null;
    }

    @Override
    public ProjectFinanceRow toProjectDomain(SubContractingCost costItem) {
        return mapSubContractingToProjectCost(costItem);
    }

    @Override
    public SubContractingCost toResource(FinanceRow cost) {
        return buildRowItem(cost, cost.getFinanceRowMetadata());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(SUBCONTRACTING_COSTS);
    }

    private SubContractingCost buildRowItem(FinanceRow cost, List<FinanceRowMetaValue> financeRowMetaValues){
        String country = "";
        for(FinanceRowMetaValue costValue : financeRowMetaValues) {
            if(costValue.getFinanceRowMetaField() != null && costValue.getFinanceRowMetaField().getTitle().equals(COST_FIELD_COUNTRY)) {
                country = costValue.getValue();
            }
        }

        return new SubContractingCost(cost.getId(), cost.getCost(), country, cost.getItem(), cost.getDescription(), cost.getTarget().getId());
    }

    private ApplicationFinanceRow mapSubContractingCost(FinanceRowItem costItem) {
        SubContractingCost subContractingCost = (SubContractingCost) costItem;
        ApplicationFinanceRow cost =  new ApplicationFinanceRow(subContractingCost.getId(), COST_KEY, subContractingCost.getName(), subContractingCost.getRole(),
                0, subContractingCost.getCost(),null,costItem.getCostType());
        cost.addCostValues(
                new FinanceRowMetaValue(cost, costFields.get(COST_FIELD_COUNTRY), subContractingCost.getCountry()));
        return cost;
    }

    private ProjectFinanceRow mapSubContractingToProjectCost(FinanceRowItem costItem) {
        SubContractingCost subContractingCost = (SubContractingCost) costItem;
        ProjectFinanceRow cost =  new ProjectFinanceRow(subContractingCost.getId(), COST_KEY, subContractingCost.getName(), subContractingCost.getRole(),
                0, subContractingCost.getCost(),null,costItem.getCostType());
        cost.addCostValues(
                new FinanceRowMetaValue(cost, costFields.get(COST_FIELD_COUNTRY), subContractingCost.getCountry()));
        return cost;
    }
}
