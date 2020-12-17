package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.viewmodel;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

import java.util.List;

public class FundingLevelPercentageViewModel extends CompetitionSetupViewModel {

    private final List<ResearchCategoryResource> categories;
    private final List<OrganisationSize> sizes;

    public FundingLevelPercentageViewModel(
            GeneralSetupViewModel generalSetupViewModel,
            List<ResearchCategoryResource> categories, List<OrganisationSize> sizes) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.categories = categories;
        this.sizes = sizes;
    }

    public List<ResearchCategoryResource> getCategories() {
        return categories;
    }

    public List<OrganisationSize> getSizes() {
        return sizes;
    }

    public String descriptionForSize(OrganisationSize size) {
        switch (size) {
            case SMALL:
                return "Micro entity or small company";
            case MEDIUM:
                return "Medium-sized company";
            case LARGE:
                return "Large-sized company";
            default:
                throw new ObjectNotFoundException("Unknown size " + size);
        }
    }

    public boolean isShowResetButton() {
        return !generalSetupViewModel.getState().isPreventEdit() &&
                generalSetupViewModel.getCompetition().getFundingRules() == FundingRules.STATE_AID &&
                !categories.isEmpty();
    }

    public boolean isShowFundingRules() {
        return generalSetupViewModel.getCompetition().getFundingRules() != FundingRules.NOT_AID;
    }
}
