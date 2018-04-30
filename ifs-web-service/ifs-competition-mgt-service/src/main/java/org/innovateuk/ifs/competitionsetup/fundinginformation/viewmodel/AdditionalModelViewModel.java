package org.innovateuk.ifs.competitionsetup.fundinginformation.viewmodel;

import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;

public class AdditionalModelViewModel extends CompetitionSetupViewModel {
    public AdditionalModelViewModel(GeneralSetupViewModel generalViewModel) {
        this.generalSetupViewModel = generalViewModel;
    }
}
