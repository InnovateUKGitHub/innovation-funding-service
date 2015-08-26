package com.worth.ifs.assembler.mapping;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.resource.OtherCost;
import com.worth.ifs.resource.OtherCostCategoryResource;

import java.util.ArrayList;
import java.util.List;

public class OtherCostMapper implements ResourceMapper<OtherCost> {
    public OtherCostCategoryResource getCostResource(CostCategory costCategory) {
        List<OtherCost> otherCosts = mapCosts(costCategory.getCosts());
        return new OtherCostCategoryResource(
                costCategory.getQuestion().getSection().getId(), costCategory.getQuestion().getId(),
                costCategory.getId(), otherCosts);
    }

    public List<OtherCost> mapCosts(List<Cost> costs) {
        List<OtherCost> otherCosts = new ArrayList<>();
        costs.stream().forEach(c -> otherCosts.add(new OtherCost(c.getId(), c.getCost(),c.getDescription())));
        return otherCosts;
    }
}
