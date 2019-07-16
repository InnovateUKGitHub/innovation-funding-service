package org.innovateuk.ifs.management.competition.setup.fundinginformation.populator;

import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.fundinginformation.viewmodel.AdditionalModelViewModel;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * populates the model for the additional info competition setup section.
 */
@Service
public class AdditionalModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.ADDITIONAL_INFO;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new AdditionalModelViewModel(generalViewModel);
    }
}
