package org.innovateuk.ifs.competitionsetup.projectdocuments.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.competitionsetup.projectdocuments.viewmodel.ProjectDocumentsViewModel;
import org.springframework.stereotype.Service;

/**
 * populates the model for the documents competition setup section.
 */
@Service
public class ProjectDocumentsPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.PROJECT_DOCUMENTS;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel,
                                                   CompetitionResource competitionResource) {
        return new ProjectDocumentsViewModel(generalViewModel);
    }
}



