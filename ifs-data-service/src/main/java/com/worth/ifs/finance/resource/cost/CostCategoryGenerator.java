package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.project.finance.domain.CostCategory;

public interface CostCategoryGenerator<T> extends Comparable<T> {

    boolean isSpendCostCategory();

    String getName();

    String getLabel();

    /**
     * Convenience method to determine if a {@link CostCategory} is equal to a {@link CostCategoryGenerator} and so needs to be generated
     *
     * @param cc
     * @param ccg
     * @return
     */
    static boolean areEqual(CostCategory cc, CostCategoryGenerator ccg) {
        return ccg.getLabel() == cc.getLabel() && ccg.getName() == cc.getName();
    }

}
