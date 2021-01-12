package org.innovateuk.ifs.application.finance.viewmodel;

import java.util.List;

public class ProjectFinanceChangesProjectFinancesViewModel {
    private List<CostChangeViewModel> entries;
    private boolean vatRegistered;
    private CostChangeViewModel vat;

    public ProjectFinanceChangesProjectFinancesViewModel(List<CostChangeViewModel> entries,
                                                         boolean vatRegistered, CostChangeViewModel vat) {
        this.entries = entries;
        this.vatRegistered = vatRegistered;
        this.vat = vat;
    }

    public List<CostChangeViewModel> getEntries() {
        return entries;
    }

    public boolean isVatRegistered() {
        return vatRegistered;
    }

    public CostChangeViewModel getVat() {
        return vat;
    }

    public CostChangeViewModel getTotalPlusVat() {
        CostChangeViewModel totalExVat = getTotalProjectCosts();
        return new CostChangeViewModel("Total project costs inclusive of VAT",
                totalExVat.getApplicationCost().add(vat.getApplicationCost()),
                totalExVat.getProjectCost().add(vat.getProjectCost()));
    }

    public CostChangeViewModel getTotalProjectCosts() {
        return entries.stream().reduce((a, b) ->
            new CostChangeViewModel("Total costs (£)",
                    a.getApplicationCost().add(b.getApplicationCost()),
                    a.getProjectCost().add(b.getProjectCost()))
        ).get();
    }

    public boolean hasChanges() {
        if (vat != null && vat.isProjectCostDifferent()) {
            return true;
        }
        return entries.stream().anyMatch(entry -> entry.isProjectCostDifferent());
    }
}
