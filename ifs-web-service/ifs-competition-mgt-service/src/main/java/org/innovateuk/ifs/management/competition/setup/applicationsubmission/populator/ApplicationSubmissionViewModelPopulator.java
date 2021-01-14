package org.innovateuk.ifs.management.competition.setup.applicationsubmission.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.viewmodel.ApplicationSubmissionViewModel;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Application Submission page in Competition Setup.
 */
@Service
public class ApplicationSubmissionViewModelPopulator implements CompetitionSetupSectionModelPopulator  {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.APPLICATION_SUBMISSION;
    }

    @Override
    public ApplicationSubmissionViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new ApplicationSubmissionViewModel(generalViewModel);
    }
}
