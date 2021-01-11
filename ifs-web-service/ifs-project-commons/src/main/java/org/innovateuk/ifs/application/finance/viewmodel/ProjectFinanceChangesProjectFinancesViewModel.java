package org.innovateuk.ifs.application.finance.viewmodel;

import java.util.List;
import java.util.Objects;

public class ProjectFinanceChangesProjectFinancesViewModel {
    private List<CostChangeViewModel> entries;

    public ProjectFinanceChangesProjectFinancesViewModel(List<CostChangeViewModel> entries) {
        this.entries = entries;
    }

    public List<CostChangeViewModel> getEntries() {
        return entries;
    }

    public CostChangeViewModel getTotalProjectCosts() {
        return entries.stream().reduce((a, b) -> {
            CostChangeViewModel result = new CostChangeViewModel();
            result.setProjectCost(a.getProjectCost().add(b.getProjectCost()));
            result.setApplicationCost(a.getApplicationCost().add(b.getApplicationCost()));
            return result;
        }).get();
    }
}
