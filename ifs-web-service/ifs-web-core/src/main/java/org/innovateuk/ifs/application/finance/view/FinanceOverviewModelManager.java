package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceOverviewViewModel;
import org.springframework.ui.Model;

import java.util.Optional;

public interface FinanceOverviewModelManager {
    //TODO: make sure this function is not going to be used anymore - IFS-3801
    void addFinanceDetails(Model model, Long competitionId, Long applicationId, Optional<Long> organisationId);
    BaseFinanceOverviewViewModel getFinanceDetailsViewModel(Long competitionId, Long applicationId);
}
