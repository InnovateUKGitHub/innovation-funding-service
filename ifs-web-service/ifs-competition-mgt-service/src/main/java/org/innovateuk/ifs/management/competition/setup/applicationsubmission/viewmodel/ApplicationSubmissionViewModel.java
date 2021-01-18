package org.innovateuk.ifs.management.competition.setup.applicationsubmission.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

import static java.lang.String.format;

/**
 * A view model to back the Application Submission selection page.
 */
public class ApplicationSubmissionViewModel extends CompetitionSetupViewModel  {

    public ApplicationSubmissionViewModel(GeneralSetupViewModel generalSetupViewModel) {
        this.generalSetupViewModel = generalSetupViewModel;
    }
}
