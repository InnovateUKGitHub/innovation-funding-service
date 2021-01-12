package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.List;

public class ProjectFinanceChangesMilestoneDifferencesViewModel {
    private List<MilestoneChangeViewModel> milestoneDifferences;
    private BigInteger applicationTotal;
    private BigInteger projectTotal;

    public ProjectFinanceChangesMilestoneDifferencesViewModel(List<MilestoneChangeViewModel> milestoneDifferences,
                                                              BigInteger applicationTotal, BigInteger projectTotal) {
        this.milestoneDifferences = milestoneDifferences;
        this.applicationTotal = applicationTotal;
        this.projectTotal = projectTotal;
    }

    public List<MilestoneChangeViewModel> getMilestoneDifferences() {
        return milestoneDifferences;
    }

    public BigInteger getApplicationTotal() {
        return applicationTotal;
    }

    public BigInteger getProjectTotal() {
        return projectTotal;
    }

    public String getTotalVariance() {
        BigInteger change = projectTotal.subtract(applicationTotal);
        String direction = (change.compareTo(BigInteger.ZERO) > 0) ? "+" : "-";

        return direction + " £" + NumberFormat.getNumberInstance().format(change.abs());
    }

    public boolean hasChanges() {
        return !milestoneDifferences.isEmpty();
    }
}
