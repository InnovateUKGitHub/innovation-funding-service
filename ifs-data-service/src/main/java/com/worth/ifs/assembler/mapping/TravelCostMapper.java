package com.worth.ifs.assembler.mapping;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.resource.TravelCost;
import com.worth.ifs.resource.TravelCostCategoryResource;

import java.util.ArrayList;
import java.util.List;

public class TravelCostMapper implements ResourceMapper<TravelCost> {
    public TravelCostCategoryResource getCostResource(CostCategory costCategory) {
        List<TravelCost> travelCosts = mapCosts(costCategory.getCosts());
        return new TravelCostCategoryResource(
                costCategory.getQuestion().getSection().getId(), costCategory.getQuestion().getId(),
                costCategory.getId(), travelCosts);
    }

    public List<TravelCost> mapCosts(List<Cost> costs) {
        List<TravelCost> travelCosts = new ArrayList<>();
        costs.stream().forEach(c -> travelCosts.add(new TravelCost(c.getId(), c.getCost(), c.getItem(), c.getQuantity())));
        return travelCosts;
    }
}
