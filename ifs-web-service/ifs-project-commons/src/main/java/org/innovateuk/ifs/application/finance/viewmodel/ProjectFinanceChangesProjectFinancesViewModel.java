package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigDecimal;
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
        return entries.stream().reduce((a, b) -> {
                BigDecimal applicationCost;
                if (a.getApplicationCost() == null || b.getApplicationCost() == null) {
                    applicationCost = null;
                } else {
                    applicationCost = a.getApplicationCost().add(b.getApplicationCost());
                }
                BigDecimal projectCost = a.getProjectCost().add(b.getProjectCost());
            return new CostChangeViewModel("Total costs (£)",
                    applicationCost,
                    projectCost);
        }).get();
    }

    public boolean hasChanges() {
        if (vat != null && vat.isProjectCostDifferent()) {
            return true;
        }
        return entries.stream().anyMatch(entry -> entry.isProjectCostDifferent());
    }
}
