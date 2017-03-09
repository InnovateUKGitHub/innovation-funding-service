package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationResearchCategoryRestService;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategoryPopulator {
    @Autowired
    private ApplicationResearchCategoryRestService applicationResearchCategoryRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    public ResearchCategoryViewModel populate(Long applicationId, Long questionId) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccessObject();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel();
        researchCategoryViewModel.setAvailableResearchCategories(categoryRestService.getResearchCategories().getSuccessObject());
        researchCategoryViewModel.setQuestionId(questionId);
        researchCategoryViewModel.setApplicationId(applicationId);
        researchCategoryViewModel.setCurrentCompetitionName(applicationResource.getCompetitionName());

        setResearchCategoryChoice(applicationResource, researchCategoryViewModel);

        return researchCategoryViewModel;
    }

    private void setResearchCategoryChoice(ApplicationResource applicationResource, ResearchCategoryViewModel researchCategoryViewModel) {

        if (applicationResource.getResearchCategory() != null && applicationResource.getResearchCategory().getId() != null) {
            researchCategoryViewModel.setSelectedResearchCategoryId(applicationResource.getResearchCategory().getId());
        }
    }
}
