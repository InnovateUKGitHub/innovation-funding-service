package com.worth.ifs.project.resource;


import java.math.BigDecimal;
import java.util.Map;

public class SpendProfileResource {

    /*
     * Holds the total eligible cost per category on the Spend Profile page
     */
    private Map<String, BigDecimal> eligibleCostPerCategoryMap;

    public Map<String, BigDecimal> getEligibleCostPerCategoryMap() {
        return eligibleCostPerCategoryMap;
    }

    public void setEligibleCostPerCategoryMap(Map<String, BigDecimal> eligibleCostPerCategoryMap) {
        this.eligibleCostPerCategoryMap = eligibleCostPerCategoryMap;
    }
}
