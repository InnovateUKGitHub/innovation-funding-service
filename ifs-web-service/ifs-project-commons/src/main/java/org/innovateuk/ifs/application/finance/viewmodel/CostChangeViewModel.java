package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigDecimal;

public class CostChangeViewModel {
    private String section;
    private BigDecimal applicationCost;
    private BigDecimal projectCost;

    public CostChangeViewModel(String section, BigDecimal applicationCost, BigDecimal projectCost) {
        this.section = section;
        this.applicationCost = applicationCost;
        this.projectCost = projectCost;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public BigDecimal getApplicationCost() {
        return applicationCost;
    }

    public BigDecimal getProjectCost() {
        return projectCost;
    }

    public BigDecimal getDifference() {
        return projectCost.subtract(applicationCost);
    }

    public boolean isProjectCostDifferent() {
        if (projectCost != null) {
            return !projectCost.equals(applicationCost);
        }
        return false;
    }

    public String getVarianceDirection() {
        boolean increase = projectCost.compareTo(applicationCost) > 0;
        if (increase) {
            return "+";
        }
        return "-";
    }

    public BigDecimal getVariance() {
        return getDifference().abs();
    }
}
