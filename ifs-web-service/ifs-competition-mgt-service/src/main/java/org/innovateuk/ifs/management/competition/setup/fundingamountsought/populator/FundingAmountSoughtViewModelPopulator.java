package org.innovateuk.ifs.management.competition.setup.fundingamountsought.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.fundingamountsought.viewmodel.FundingAmountSoughtViewModel;
import org.springframework.stereotype.Service;

/**
 * Service to populate the funding amount sought page in Competition Setup.
 */
@Service
public class FundingAmountSoughtViewModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.FUNDING_AMOUNT_SOUGHT;
    }

    @Override
    public FundingAmountSoughtViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new FundingAmountSoughtViewModel(generalViewModel);
    }
}
