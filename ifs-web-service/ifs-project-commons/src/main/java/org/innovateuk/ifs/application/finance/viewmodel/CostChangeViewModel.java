package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigDecimal;

public class CostChangeViewModel {
    private String section;
    private BigDecimal applicationCost;
    private BigDecimal projectCost;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public BigDecimal getApplicationCost() {
        return applicationCost;
    }

    public void setApplicationCost(BigDecimal applicationCost) {
        this.applicationCost = applicationCost;
    }

    public BigDecimal getProjectCost() {
        return projectCost;
    }

    public void setProjectCost(BigDecimal projectCost) {
        this.projectCost = projectCost;
    }

    public BigDecimal getDifference() {
        return projectCost.subtract(applicationCost);
    }

    public boolean isProjectCostDifferent() {
        return !projectCost.equals(applicationCost);
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
