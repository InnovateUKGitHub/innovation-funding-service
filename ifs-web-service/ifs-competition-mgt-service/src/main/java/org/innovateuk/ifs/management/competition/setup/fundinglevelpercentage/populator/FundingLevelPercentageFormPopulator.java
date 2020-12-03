package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelMaximumForm;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelPercentageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelMaximumForm.singleValueForm;

/**
 * Form populator for the eligibility competition setup section.
 */
@Service
public class FundingLevelPercentageFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.FUNDING_LEVEL_PERCENTAGE;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        FundingLevelPercentageForm competitionSetupForm = new FundingLevelPercentageForm();

        List<GrantClaimMaximumResource> maximums = grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competitionResource.getId()).getSuccess();
        if (competitionResource.getResearchCategories().isEmpty()) {
            competitionSetupForm.getMaximums().add(singleValueForm(maximums.stream().findAny().map(GrantClaimMaximumResource::getMaximum).orElse(null)));
        } else {
            competitionSetupForm.setMaximums(maximums.stream().map(FundingLevelMaximumForm::fromGrantClaimMaximumResource).collect(Collectors.toList()));
        }
        return competitionSetupForm;
    }
}
