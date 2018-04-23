package org.innovateuk.ifs.competitionsetup.form.fundinginformation.viewmodel;

import org.innovateuk.ifs.competitionsetup.form.common.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.form.common.viewmodel.GeneralSetupViewModel;

public class AdditionalModelViewModel extends CompetitionSetupViewModel {
    public AdditionalModelViewModel(GeneralSetupViewModel generalViewModel) {
        this.generalSetupViewModel = generalViewModel;
    }
}
