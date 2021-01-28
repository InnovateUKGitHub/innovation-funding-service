package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator;

import com.google.common.collect.Multimap;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.FundingRules;
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

    public CompetitionSetupForm populateForm(CompetitionResource competitionResource, FundingRules fundingRules) {
        FundingLevelPercentageForm competitionSetupForm = new FundingLevelPercentageForm();

        if (competitionResource.isNonFinanceType()) {
            competitionSetupForm.getMaximums().add(newArrayList(singleValueForm(null, null)));
        } else {
            List<GrantClaimMaximumResource> maximums = getMaximums(competitionResource, fundingRules);
            if (competitionResource.getResearchCategories().isEmpty()) {
                populateSingleForm(maximums, competitionSetupForm);
            } else {
                populateTableForm(competitionResource, maximums, competitionSetupForm);
            }
        }
        return competitionSetupForm;
    }

    private void populateSingleForm(List<GrantClaimMaximumResource> maximums, FundingLevelPercentageForm competitionSetupForm) {
        if (maximums.stream().allMatch(max -> max.getFundingRules() != null)) {
            Multimap<FundingRules, GrantClaimMaximumResource> map = index(maximums, GrantClaimMaximumResource::getFundingRules);
            competitionSetupForm.getMaximums().add(
                    map.asMap().entrySet().stream().map(e -> singleValueForm(e.getValue().stream().findAny().map(GrantClaimMaximumResource::getMaximum).orElse(null), e.getKey())).collect(toList()));
        } else {
            competitionSetupForm.getMaximums().add(newArrayList(singleValueForm(maximums.stream().findAny().map(GrantClaimMaximumResource::getMaximum).orElse(null), null)));
        }
    }

    private void populateTableForm(CompetitionResource competitionResource, List<GrantClaimMaximumResource> maximums, FundingLevelPercentageForm competitionSetupForm) {
        List<FundingLevelMaximumForm> forms = maximums.stream()
                .filter(maximum -> competitionResource.getResearchCategories().contains(maximum.getResearchCategory().getId()))
                .map(FundingLevelMaximumForm::fromGrantClaimMaximumResource).collect(Collectors.toList());
        Multimap<OrganisationSize, FundingLevelMaximumForm> map = index(forms, FundingLevelMaximumForm::getOrganisationSize);
        List<List<FundingLevelMaximumForm>> listOfLists = map.asMap().values().stream().map(ArrayList::new).collect(toList());
        competitionSetupForm.setMaximums(listOfLists);
    }

    private List<GrantClaimMaximumResource> getMaximums(CompetitionResource competitionResource, FundingRules fundingRules) {
        List<GrantClaimMaximumResource> maximums =  grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competitionResource.getId()).getSuccess();
        if (fundingRules == null) {
            return maximums;
        }
        return maximums.stream().filter(max -> max.getFundingRules() == fundingRules).collect(toList());
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        return populateForm(competitionResource, null);
    }
}
