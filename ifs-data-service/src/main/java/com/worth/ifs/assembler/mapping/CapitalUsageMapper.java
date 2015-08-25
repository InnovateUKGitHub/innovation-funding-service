package com.worth.ifs.assembler.mapping;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.domain.CostValue;
import com.worth.ifs.resource.CapitalUsage;
import com.worth.ifs.resource.CapitalUsageCategoryResource;

import java.util.ArrayList;
import java.util.List;

public class CapitalUsageMapper implements ResourceMapper<CapitalUsage> {
    public final String COST_FIELD_EXISTING = "existing";
    public final String COST_FIELD_RESIDUAL_VALUE = "residual_value";
    public final String COST_FIELD_UTILISATION = "utilisation";

    public CapitalUsageCategoryResource getCostResource(CostCategory costCategory) {
        List<CapitalUsage> capitalUsages = mapCosts(costCategory.getCosts());
        return new CapitalUsageCategoryResource(costCategory.getId(), capitalUsages, costCategory.getQuestion().getId(), costCategory.getQuestion().getSection().getId());
    }

    public List<CapitalUsage> mapCosts(List<Cost> costs) {
        List<CapitalUsage> capitalUsages = new ArrayList<>();
        costs.stream().forEach(c -> capitalUsages.add(mapCapitalUsage(c)));

        return capitalUsages;
    }

    private CapitalUsage mapCapitalUsage(Cost cost) {
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

}
