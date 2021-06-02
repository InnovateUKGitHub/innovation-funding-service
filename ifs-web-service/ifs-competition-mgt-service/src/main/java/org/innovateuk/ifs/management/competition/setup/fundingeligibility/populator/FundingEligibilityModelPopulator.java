package org.innovateuk.ifs.management.competition.setup.fundingeligibility.populator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.fundingeligibility.viewmodel.FundingEligibilityViewModel;
import org.innovateuk.ifs.management.competition.setup.service.CategoryFormatter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * populates the model for the eligibility competition setup section.
 */
@Service
public class FundingEligibilityModelPopulator implements CompetitionSetupSectionModelPopulator {

    private CategoryRestService categoryRestService;
    private CategoryFormatter categoryFormatter;

    public FundingEligibilityModelPopulator(CategoryRestService categoryRestService,
                                            CategoryFormatter categoryFormatter) {
        this.categoryRestService = categoryRestService;
        this.categoryFormatter = categoryFormatter;
    }

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.FUNDING_ELIGIBILITY;
    }

    @Override
    public CompetitionSetupViewModel populateModel(
            GeneralSetupViewModel generalViewModel,
            CompetitionResource competitionResource
    ) {
        List<ResearchCategoryResource> researchCategories = categoryRestService.getResearchCategories().getSuccess();
        String researchCategoriesFormatted = categoryFormatter.format(
                competitionResource.getResearchCategories(),
                researchCategories
        );

        return new FundingEligibilityViewModel(
                generalViewModel,
                researchCategories,
                researchCategoriesFormatted
        );
    }
}
