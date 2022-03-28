package org.innovateuk.ifs.management.competition.setup.applicationassessment.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

/**
 * A view model to back the Application assessment selection page.
 */
public class ApplicationAssessmentViewModel extends CompetitionSetupViewModel {

    public ApplicationAssessmentViewModel(GeneralSetupViewModel generalSetupViewModel) {
        this.generalSetupViewModel = generalSetupViewModel;
    }
}
