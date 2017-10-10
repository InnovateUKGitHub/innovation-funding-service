package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

public abstract class CompetitionSetupViewModel {
    protected GeneralSetupViewModel generalSetupViewModel;

    public GeneralSetupViewModel getGeneral() {
        return generalSetupViewModel;
    }
}
