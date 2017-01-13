package org.innovateuk.ifs.application.finance.view;

import org.springframework.ui.Model;

public interface FinanceOverviewModelManager {
    void addFinanceDetails(Model model, Long competitionId, Long applicationId);
}
