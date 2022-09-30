package org.innovateuk.ifs.management.competition.setup.projectimpact.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.projectimpact.viewmodel.ProjectImpactViewModel;
import org.springframework.stereotype.Service;

@Service
public class SupportingDocumentViewModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.PROJECT_IMPACT;
    }

    @Override
    public ProjectImpactViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new ProjectImpactViewModel(generalViewModel);
    }
}
