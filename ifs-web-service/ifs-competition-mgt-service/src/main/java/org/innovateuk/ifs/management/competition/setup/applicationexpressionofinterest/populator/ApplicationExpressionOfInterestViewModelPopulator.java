package org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.viewmodel.ApplicationExpressionOfInterestViewModel;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.springframework.stereotype.Service;

@Service
public class ApplicationExpressionOfInterestViewModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.APPLICATION_EXPRESSION_OF_INTEREST;
    }

    @Override
    public ApplicationExpressionOfInterestViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new ApplicationExpressionOfInterestViewModel(generalViewModel);
    }
}
