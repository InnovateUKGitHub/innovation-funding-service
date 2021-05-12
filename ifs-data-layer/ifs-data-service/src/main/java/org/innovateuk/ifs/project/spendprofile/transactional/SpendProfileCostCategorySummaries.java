package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;

import java.util.List;

/**
 * Holder of summary information used to generate Spend Profiles
 */
public class SpendProfileCostCategorySummaries {

    private List<SpendProfileCostCategorySummary> costs;
    private CostCategoryType costCategoryType;

    private SpendProfileCostCategorySummaries(){

    }

    public SpendProfileCostCategorySummaries(List<SpendProfileCostCategorySummary> costs, CostCategoryType costCategoryType) {
        this.costs = costs;
        this.costCategoryType = costCategoryType;
    }

    public List<SpendProfileCostCategorySummary> getCosts() {
        return costs;
    }

    public void setCosts(List<SpendProfileCostCategorySummary> costs) {
        this.costs = costs;
    }

    public CostCategoryType getCostCategoryType() {
        return costCategoryType;
    }

    public void setCostCategoryType(CostCategoryType costCategoryType) {
        this.costCategoryType = costCategoryType;
    }
}
