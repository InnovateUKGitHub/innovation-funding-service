package org.innovateuk.ifs.competitionsetup.projectdocuments.viewmodel;

import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;

public class ProjectDocumentViewModel extends CompetitionSetupViewModel {

    public ProjectDocumentViewModel(GeneralSetupViewModel generalSetupViewModel) {
        this.generalSetupViewModel = generalSetupViewModel;
    }
}
