package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.viewmodel.LeadInternationalOrganisationViewModel;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.LEAD_INTERNATIONAL_ORGANISATION;

@Service
public class LeadInternationalOrganisationViewModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return LEAD_INTERNATIONAL_ORGANISATION;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new LeadInternationalOrganisationViewModel(generalViewModel);
    }
}
