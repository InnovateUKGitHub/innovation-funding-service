package org.innovateuk.ifs.management.competition.setup.fundingamountsought.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.fundingamountsought.form.FundingAmountSoughtForm;
import org.springframework.stereotype.Service;

@Service
public class FundingAmountSoughtModelPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.FUNDING_AMOUNT_SOUGHT;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        FundingAmountSoughtForm amountSoughtForm = new FundingAmountSoughtForm();

        amountSoughtForm.setFundingAmountSoughtApplicable(competitionResource.getCompetitionApplicationConfigResource().isMaximumFundingSoughtEnabled());

        return amountSoughtForm;
    }
}
