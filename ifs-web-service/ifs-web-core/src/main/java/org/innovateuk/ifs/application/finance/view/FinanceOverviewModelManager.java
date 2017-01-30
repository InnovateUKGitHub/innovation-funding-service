package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceOverviewViewModel;
import org.springframework.ui.Model;

public interface FinanceOverviewModelManager {
    //TODO: INFUND-7849 - make sure this function is not going to be used anymore
    void addFinanceDetails(Model model, Long competitionId, Long applicationId);
    BaseFinanceOverviewViewModel getFinanceDetailsViewModel(Long competitionId, Long applicationId);
}
