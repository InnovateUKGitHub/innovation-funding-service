package org.innovateuk.ifs.competitionsetup.documents.viewmodel;

import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;


public class DocumentsViewModel extends CompetitionSetupViewModel {

    public DocumentsViewModel(
            GeneralSetupViewModel generalSetupViewModel
    ) {
        this.generalSetupViewModel = generalSetupViewModel;
    }
}
