package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator;

import com.google.common.collect.Multimap;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelMaximumForm;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelPercentageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Multimaps.index;
import static java.util.stream.Collectors.toList;
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
            competitionSetupForm.getMaximums().add(newArrayList(singleValueForm(maximums.stream().findAny().map(GrantClaimMaximumResource::getMaximum).orElse(null))));
        } else {
            List<FundingLevelMaximumForm> forms = maximums.stream()
                    .filter(maximum -> competitionResource.getResearchCategories().contains(maximum.getResearchCategory().getId()))
                    .map(FundingLevelMaximumForm::fromGrantClaimMaximumResource).collect(Collectors.toList());
            Multimap<OrganisationSize, FundingLevelMaximumForm> map = index(forms, FundingLevelMaximumForm::getOrganisationSize);
            List<List<FundingLevelMaximumForm>> listOfLists = map.asMap().values().stream().map(ArrayList::new).collect(toList());
            competitionSetupForm.setMaximums(listOfLists);
        }
        return competitionSetupForm;
    }
}
