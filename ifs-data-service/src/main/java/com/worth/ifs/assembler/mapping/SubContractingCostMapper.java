package com.worth.ifs.assembler.mapping;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.domain.CostValue;
import com.worth.ifs.resource.SubContractingCost;
import com.worth.ifs.resource.SubContractingCostCategoryResource;

import java.util.ArrayList;
import java.util.List;

public class SubContractingCostMapper implements ResourceMapper<SubContractingCost> {
    public final String COST_FIELD_COUNTRY = "country";

    public SubContractingCostCategoryResource getCostResource(CostCategory costCategory) {
        List<SubContractingCost> subContractingCosts = mapCosts(costCategory.getCosts());
        return new SubContractingCostCategoryResource(
                costCategory.getQuestion().getSection().getId(), costCategory.getQuestion().getId(),
                costCategory.getId(), subContractingCosts);
    }

    public List<SubContractingCost> mapCosts(List<Cost> costs) {
        List<SubContractingCost> subContractingCosts = new ArrayList<>();
        costs.stream().forEach(c -> subContractingCosts.add(mapCapitalUsage(c)));
        return subContractingCosts;
    }

    private SubContractingCost mapCapitalUsage(Cost cost) {
        String country = "";

        for(CostValue costValue : cost.getCostValues()) {
            if(costValue.getCostField().getTitle().equals(COST_FIELD_COUNTRY)) {
                country = costValue.getValue();
            }
        }

        return new SubContractingCost(cost.getId(), cost.getCost(), country, cost.getItem(), cost.getDescription());
    }
}
