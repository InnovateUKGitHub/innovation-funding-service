package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.finance.resource.CostCategoryResource;
import com.worth.ifs.project.finance.resource.CostCategoryTypeResource;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_EVEN;

/**
 * Holder of summary information used to generate Spend Profiles
 */
class SpendProfileCostCategorySummaries {

    private List<SpendProfileCostCategorySummary> costs;
    private CostCategoryTypeResource costCategoryType;

    public SpendProfileCostCategorySummaries(List<SpendProfileCostCategorySummary> costs, CostCategoryTypeResource costCategoryType) {
        this.costs = costs;
        this.costCategoryType = costCategoryType;
    }

    public List<SpendProfileCostCategorySummary> getCosts() {
        return costs;
    }

    public void setCosts(List<SpendProfileCostCategorySummary> costs) {
        this.costs = costs;
    }

    public CostCategoryTypeResource getCostCategoryType() {
        return costCategoryType;
    }

    public void setCostCategoryType(CostCategoryTypeResource costCategoryType) {
        this.costCategoryType = costCategoryType;
    }
}
