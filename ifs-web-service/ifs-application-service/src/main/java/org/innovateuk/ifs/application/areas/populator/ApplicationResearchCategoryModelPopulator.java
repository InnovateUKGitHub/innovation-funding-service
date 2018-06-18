package org.innovateuk.ifs.application.areas.populator;

import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategoryModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private FinanceService financeService;

    public ResearchCategoryViewModel populate(ApplicationResource applicationResource, Long questionId) {

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel();
        researchCategoryViewModel.setAvailableResearchCategories(categoryRestService.getResearchCategories()
                .getSuccess());
        researchCategoryViewModel.setQuestionId(questionId);
        researchCategoryViewModel.setApplicationId(applicationResource.getId());
        researchCategoryViewModel.setCurrentCompetitionName(applicationResource.getCompetitionName());

        setHasApplicationFinances(researchCategoryViewModel, applicationResource);

        return researchCategoryViewModel;
    }

    private void setHasApplicationFinances(ResearchCategoryViewModel researchCategoryViewModel,
                                           ApplicationResource applicationResource) {
        if (applicationResource.getResearchCategory() != null
                && applicationResource.getResearchCategory().getId() != null) {
            researchCategoryViewModel.setHasApplicationFinances(financeService.getApplicationFinanceDetails
                    (applicationResource.getId())
                    .stream()
                    .anyMatch(applicationFinanceResource -> applicationFinanceResource.getOrganisationSize() != null));
        } else {
            researchCategoryViewModel.setHasApplicationFinances(false);
        }
    }
}
