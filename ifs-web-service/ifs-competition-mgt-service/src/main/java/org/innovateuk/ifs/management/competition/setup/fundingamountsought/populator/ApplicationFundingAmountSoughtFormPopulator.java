package org.innovateuk.ifs.management.competition.setup.fundingamountsought.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.fundingamountsought.form.ApplicationFundingAmountSoughtForm;

public class ApplicationFundingAmountSoughtFormPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.FUNDING_AMOUNT_SOUGHT;
    }

    @Override
    public ApplicationFundingAmountSoughtForm populateForm(CompetitionResource competitionResource) {
        return new ApplicationFundingAmountSoughtForm(competitionResource.isHasAssessmentStage());
    }
}
