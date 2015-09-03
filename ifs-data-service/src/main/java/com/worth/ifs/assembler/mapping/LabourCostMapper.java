package com.worth.ifs.assembler.mapping;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.resource.LabourCategoryResource;
import com.worth.ifs.resource.LabourCost;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LabourCostMapper implements ResourceMapper<LabourCost> {
    public final String WORKING_DAYS_PER_YEAR = "Working days per year";

    public LabourCategoryResource getCostResource(CostCategory costCategory) {
        Integer workingDaysPerYear = getWorkingDaysPerYear(costCategory);
        List<LabourCost> labourCost = mapCosts(costCategory.getCosts());
        return new LabourCategoryResource(costCategory.getId(), workingDaysPerYear, labourCost, costCategory.getQuestion().getId(), costCategory.getQuestion().getSection().getId());
    }

    public List<LabourCost> mapCosts(List<Cost> costs) {
        List<LabourCost> labourCosts = new ArrayList<>();
        costs.stream().filter(c -> !c.getDescription().equals(WORKING_DAYS_PER_YEAR)).forEach(l -> labourCosts.add(
                new LabourCost(l.getId(), l.getItem(), l.getCost(), l.getQuantity())));
        return labourCosts;
    }

    private Integer getWorkingDaysPerYear(CostCategory labourCostCategory) {
        Optional<Cost> cost = labourCostCategory.getCosts().stream().filter(c -> c.getDescription().equals(WORKING_DAYS_PER_YEAR)).findFirst();
        return cost.isPresent() ? cost.get().getQuantity() : 0;
    }
}
