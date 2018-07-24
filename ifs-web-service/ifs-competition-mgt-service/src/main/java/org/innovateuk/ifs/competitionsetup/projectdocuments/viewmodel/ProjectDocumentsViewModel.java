package org.innovateuk.ifs.competitionsetup.projectdocuments.viewmodel;

import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;

public class ProjectDocumentsViewModel extends CompetitionSetupViewModel {

    public ProjectDocumentsViewModel(GeneralSetupViewModel generalSetupViewModel) {
        this.generalSetupViewModel = generalSetupViewModel;
    }
}
