package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.viewmodel;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

import java.util.List;

public class FundingLevelPercentageViewModel extends CompetitionSetupViewModel {

    private final List<ResearchCategoryResource> categories;
    private final List<OrganisationSize> sizes;
    private final boolean dualFunding;
    private final boolean hasFundingRulesData;

    public FundingLevelPercentageViewModel(
            GeneralSetupViewModel generalSetupViewModel,
            List<ResearchCategoryResource> categories, List<OrganisationSize> sizes, boolean hasFundingRulesData,
            boolean dualFunding) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.categories = categories;
        this.sizes = sizes;
        this.hasFundingRulesData = hasFundingRulesData;
        this.dualFunding = dualFunding;
    }

    public List<ResearchCategoryResource> getCategories() {
        return categories;
    }

    public List<OrganisationSize> getSizes() {
        return sizes;
    }

    public boolean isDualFunding() {
        return dualFunding;
    }

    public boolean isShowResetButton() {
        return !generalSetupViewModel.getState().isPreventEdit() &&
                generalSetupViewModel.getCompetition().getFundingRules() == FundingRules.STATE_AID &&
                !categories.isEmpty();
    }

    public boolean isShowFundingRules() {
        return generalSetupViewModel.getCompetition().getFundingRules() != FundingRules.NOT_AID
                 && !dualFunding;
    }

    public boolean isHasFundingRulesData() {
        return hasFundingRulesData;
    }
}
