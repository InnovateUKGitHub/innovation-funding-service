package org.innovateuk.ifs.management.competition.setup.applicationassessment.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationassessment.viewmodel.ApplicationAssessmentViewModel;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Application assessment page in Competition Setup.
 */
@Service
public class ApplicationAssessmentViewModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.APPLICATION_ASSESSMENT;
    }

    @Override
    public ApplicationAssessmentViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new ApplicationAssessmentViewModel(generalViewModel);
    }
}
