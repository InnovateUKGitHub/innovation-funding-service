package org.innovateuk.ifs.management.competition.setup.fundingeligibility.viewmodel;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

import java.util.List;

public class FundingEligibilityViewModel extends CompetitionSetupViewModel {

    private List<ResearchCategoryResource> researchCategories;
    private String researchCategoriesFormatted;

    public FundingEligibilityViewModel(
            GeneralSetupViewModel generalSetupViewModel,
            List<ResearchCategoryResource> researchCategories,
            String researchCategoriesFormatted
    ) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.researchCategories = researchCategories;
        this.researchCategoriesFormatted = researchCategoriesFormatted;
    }

    public List<ResearchCategoryResource> getResearchCategories() {
        return researchCategories;
    }

    public String getResearchCategoriesFormatted() {
        return researchCategoriesFormatted;
    }
}
