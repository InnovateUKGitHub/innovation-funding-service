package org.innovateuk.ifs.application.finance.viewmodel;

import java.util.List;

public class ProjectFinanceChangesFinanceSummaryViewModel {
    private List<CostChangeViewModel> entries;

    public ProjectFinanceChangesFinanceSummaryViewModel(List<CostChangeViewModel> entries) {
        this.entries = entries;
    }

    public List<CostChangeViewModel> getEntries() {
        return entries;
    }

}
