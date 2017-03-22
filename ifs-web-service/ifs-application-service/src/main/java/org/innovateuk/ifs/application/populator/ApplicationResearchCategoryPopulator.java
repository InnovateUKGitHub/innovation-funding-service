package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationResearchCategoryRestService;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategoryPopulator extends BaseModelPopulator {

    @Autowired
    private ApplicationResearchCategoryRestService applicationResearchCategoryRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private ProcessRoleService processRoleService;

    public ResearchCategoryViewModel populate(Long applicationId, Long questionId, Long userId) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccessObject();

        ResearchCategoryViewModel researchCategoryViewModel = new ResearchCategoryViewModel();
        researchCategoryViewModel.setAvailableResearchCategories(categoryRestService.getResearchCategories().getSuccessObject());
        researchCategoryViewModel.setQuestionId(questionId);
        researchCategoryViewModel.setApplicationId(applicationId);
        researchCategoryViewModel.setCurrentCompetitionName(applicationResource.getCompetitionName());

        setResearchCategoryChoice(applicationResource, researchCategoryViewModel);
        setHasApplicationFinances(researchCategoryViewModel, userId, applicationId);

        return researchCategoryViewModel;
    }

    private void setResearchCategoryChoice(ApplicationResource applicationResource, ResearchCategoryViewModel researchCategoryViewModel) {

        if (applicationResource.getResearchCategory() != null && applicationResource.getResearchCategory().getId() != null) {
            researchCategoryViewModel.setSelectedResearchCategoryId(applicationResource.getResearchCategory().getId());
        }
    }

    private void setHasApplicationFinances(ResearchCategoryViewModel researchCategoryViewModel, Long userId, Long applicationId) {

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(applicationId);

        getUserOrganisation(userId, userApplicationRoles).ifPresent(org -> researchCategoryViewModel.setHasApplicationFinances(
                financeService.getApplicationFinanceDetails(userId, applicationId, org.getId()) != null));
    }
}
