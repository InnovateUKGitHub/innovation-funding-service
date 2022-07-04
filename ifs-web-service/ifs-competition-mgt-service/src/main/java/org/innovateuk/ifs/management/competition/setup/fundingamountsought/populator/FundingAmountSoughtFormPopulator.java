package org.innovateuk.ifs.management.competition.setup.fundingamountsought.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.fundingamountsought.form.FundingAmountSoughtForm;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Application funding amount sought form in Competition Setup.
 */
@Service
public class FundingAmountSoughtFormPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.FUNDING_AMOUNT_SOUGHT;
    }

    @Override
    public FundingAmountSoughtForm populateForm(CompetitionResource competitionResource) {
        return new FundingAmountSoughtForm(competitionResource.getCompetitionApplicationConfigResource().isMaximumFundingSoughtEnabled());
    }
}
