package org.innovateuk.ifs.management.competition.setup.projectdocument.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

public class ProjectDocumentViewModel extends CompetitionSetupViewModel {

    public ProjectDocumentViewModel(GeneralSetupViewModel generalSetupViewModel) {
        this.generalSetupViewModel = generalSetupViewModel;
    }
}
