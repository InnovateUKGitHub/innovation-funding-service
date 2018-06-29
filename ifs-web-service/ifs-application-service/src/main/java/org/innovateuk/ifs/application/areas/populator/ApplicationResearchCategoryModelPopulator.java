package org.innovateuk.ifs.application.areas.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.populator.AbstractLeadOnlyModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategoryModelPopulator extends AbstractLeadOnlyModelPopulator {

    private CategoryRestService categoryRestService;
    private FinanceService financeService;
    private UserService userService;

    public ApplicationResearchCategoryModelPopulator(final ApplicantRestService applicantRestService,
                                                     final CategoryRestService categoryRestService,
                                                     final FinanceService financeService,
                                                     final QuestionRestService questionRestService,
                                                     final UserService userService) {
        super(applicantRestService, questionRestService);
        this.categoryRestService = categoryRestService;
        this.financeService = financeService;
        this.userService = userService;
    }

    public ResearchCategoryViewModel populate(ApplicationResource applicationResource,
                                              long loggedInUserId,
                                              Long questionId,
                                              boolean useNewApplicantMenu) {
        boolean hasApplicationFinances = hasApplicationFinances(applicationResource);
        List<ResearchCategoryResource> researchCategories = categoryRestService.getResearchCategories().getSuccess();
        boolean canMarkAsComplete = userService.isLeadApplicant(loggedInUserId, applicationResource);
        boolean complete = isComplete(applicationResource, loggedInUserId);
        boolean readonly = !(canMarkAsComplete && !complete);

        return new ResearchCategoryViewModel(applicationResource.getCompetitionName(),
                applicationResource.getId(),
                questionId,
                researchCategories,
                hasApplicationFinances,
                useNewApplicantMenu,
                !isCompetitionOpen(applicationResource),
                complete,
                canMarkAsComplete,
                readonly);
    }

    private boolean hasApplicationFinances(ApplicationResource applicationResource) {
        if (applicationResource.getResearchCategory() != null
                && applicationResource.getResearchCategory().getId() != null) {
            return financeService.getApplicationFinanceDetails
                    (applicationResource.getId())
                    .stream()
                    .anyMatch(applicationFinanceResource -> applicationFinanceResource.getOrganisationSize() != null);
        }
        return false;
    }
}
