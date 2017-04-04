package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategoryPopulator extends BaseModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private FinanceService financeService;

    public ResearchCategoryViewModel populate(Long applicationId, Long questionId) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccessObject();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel();
        researchCategoryViewModel.setAvailableResearchCategories(categoryRestService.getResearchCategories().getSuccessObject());
        researchCategoryViewModel.setQuestionId(questionId);
        researchCategoryViewModel.setApplicationId(applicationId);
        researchCategoryViewModel.setCurrentCompetitionName(applicationResource.getCompetitionName());

        setResearchCategoryChoice(applicationResource, researchCategoryViewModel);
        setHasApplicationFinances(researchCategoryViewModel, applicationId);

        return researchCategoryViewModel;
    }

    private void setResearchCategoryChoice(ApplicationResource applicationResource, ResearchCategoryViewModel researchCategoryViewModel) {

        if (applicationResource.getResearchCategory() != null && applicationResource.getResearchCategory().getId() != null) {
            researchCategoryViewModel.setSelectedResearchCategoryId(applicationResource.getResearchCategory().getId());
        }
    }

    private void setHasApplicationFinances(ResearchCategoryViewModel researchCategoryViewModel, Long applicationId) {

        boolean applicationFinanceDetailsEntered = financeService.getApplicationFinanceDetails(applicationId).stream()
                .filter(applicationFinanceResource -> applicationFinanceResource.getOrganisationSize() != null).findFirst().isPresent();

        researchCategoryViewModel.setHasApplicationFinances(applicationFinanceDetailsEntered);
    }
}
