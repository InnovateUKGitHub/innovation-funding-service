package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.TermsAndConditionsViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates the model for the terms and condition competition setup section.
 */
@Service
public class TermsAndConditionModelPopulator implements CompetitionSetupSectionModelPopulator {
    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.TERMS_AND_CONDITIONS;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel,
                                                   CompetitionResource competitionResource) {
        return new TermsAndConditionsViewModel(generalViewModel);
    }
}
