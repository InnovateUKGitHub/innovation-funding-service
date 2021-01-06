package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigDecimal;

public class CostChangeViewModel {
    private BigDecimal applicationCost;
    private BigDecimal projectCost;

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

    public String getVariance() {
        boolean increase = projectCost.compareTo(applicationCost) > 0;
        String change = increase ? "+" : "-";
        BigDecimal diff = getDifference().abs();
        return change + " " + diff;
    }
}
