package org.innovateuk.ifs.management.competition.setup.applicationsubmission.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * A view model to back the Application Submission selection page.
 */
public class ApplicationSubmissionViewModel extends CompetitionSetupViewModel  {

    public ApplicationSubmissionViewModel(GeneralSetupViewModel generalSetupViewModel) {
        this.generalSetupViewModel = generalSetupViewModel;
    }
}
