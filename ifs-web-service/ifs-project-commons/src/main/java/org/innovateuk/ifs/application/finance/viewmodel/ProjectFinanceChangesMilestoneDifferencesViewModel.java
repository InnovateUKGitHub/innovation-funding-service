package org.innovateuk.ifs.application.finance.viewmodel;

import java.util.List;

public class ProjectFinanceChangesMilestoneDifferencesViewModel {
    private List<MilestoneChangeViewModel> milestoneDifferences;

    public ProjectFinanceChangesMilestoneDifferencesViewModel(List<MilestoneChangeViewModel> milestoneDifferences) {
        this.milestoneDifferences = milestoneDifferences;
    }

    public List<MilestoneChangeViewModel> getMilestoneDifferences() {
        return milestoneDifferences;
    }

    public boolean hasChanges() {
        return !milestoneDifferences.isEmpty();
    }
}
