package org.innovateuk.ifs.management.competition.setup.fundingamountsought.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

/**
 * A view model to back the funding amount sought selection page.
 */
public class FundingAmountSoughtViewModel extends CompetitionSetupViewModel {

    public FundingAmountSoughtViewModel(GeneralSetupViewModel generalSetupViewModel) {
        this.generalSetupViewModel = generalSetupViewModel;
    }
}
