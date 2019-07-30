package org.innovateuk.ifs.management.competition.setup.fundinginformation.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

public class AdditionalModelViewModel extends CompetitionSetupViewModel {
    public AdditionalModelViewModel(GeneralSetupViewModel generalViewModel) {
        this.generalSetupViewModel = generalViewModel;
    }
}
