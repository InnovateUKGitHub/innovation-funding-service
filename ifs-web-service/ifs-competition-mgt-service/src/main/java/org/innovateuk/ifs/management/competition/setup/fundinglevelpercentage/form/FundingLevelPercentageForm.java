package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class FundingLevelPercentageForm extends CompetitionSetupForm {

    private List<List<FundingLevelMaximumForm>> maximums = new ArrayList<>();

    public List<List<FundingLevelMaximumForm>> getMaximums() {
        return maximums;
    }

    public void setMaximums(List<List<FundingLevelMaximumForm>> maximums) {
        this.maximums = maximums;
    }

    public int indexForSize(List<List<FundingLevelMaximumForm>> cells, OrganisationSize size) {
        return IntStream.range(0, cells.size())
                .filter(index -> cells.get(index).get(0).getOrganisationSize() == size)
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("No matching maximum on form"));
    }

    public int indexForCategory(List<List<FundingLevelMaximumForm>> cells, ResearchCategoryResource category) {
        return IntStream.range(0, cells.get(0).size())
                .filter(index -> cells.get(0).get(index).getCategoryId().equals(category.getId()))
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("No matching maximum on form"));
    }

    public List<List<FundingLevelMaximumForm>> getSubsidyControlMaximums() {
        return getMaximumsForFundingRule(FundingRules.SUBSIDY_CONTROL);
    }

    public List<List<FundingLevelMaximumForm>> getStateAidMaximums() {
        return getMaximumsForFundingRule(FundingRules.STATE_AID);
    }

    private List<List<FundingLevelMaximumForm>> getMaximumsForFundingRule(FundingRules fundingRules) {
        return maximums.stream()
                .map(list ->
                        list.stream()
                                .filter(max -> max.getFundingRules() == fundingRules)
                                .collect(toList())
                )
                .collect(toList());
    }
}
