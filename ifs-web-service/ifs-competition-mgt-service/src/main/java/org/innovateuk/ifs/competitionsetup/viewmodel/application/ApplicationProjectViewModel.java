package org.innovateuk.ifs.competitionsetup.viewmodel.application;

import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

public class ApplicationProjectViewModel extends CompetitionSetupSubsectionViewModel {
    public ApplicationProjectViewModel(GeneralSetupViewModel generalSetupViewModel) {
        this.generalSetupViewModel = generalSetupViewModel;
    }
}
