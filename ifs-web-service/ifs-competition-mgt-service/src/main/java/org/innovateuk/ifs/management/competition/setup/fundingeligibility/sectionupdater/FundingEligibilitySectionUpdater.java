package org.innovateuk.ifs.management.competition.setup.fundingeligibility.sectionupdater;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.fundingeligibility.form.FundingEligibilityResearchCategoryForm;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.FUNDING_ELIGIBILITY;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Competition setup section saver for the eligibility section.
 */
@Service
public class FundingEligibilitySectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return FUNDING_ELIGIBILITY;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(
            CompetitionResource competition,
            CompetitionSetupForm competitionSetupForm
    ) {
        FundingEligibilityResearchCategoryForm projectEligibilityForm = (FundingEligibilityResearchCategoryForm) competitionSetupForm;
        if (!projectEligibilityForm.getResearchCategoriesApplicable()) {
            projectEligibilityForm.getResearchCategoryId().clear();
        }
        return handleResearchCategoryApplicableChanges(competition, projectEligibilityForm)
                .andOnSuccess((researchCategoriesYesNoChanged) -> {
                    boolean researchCategoriesChanged = !competition.getResearchCategories().equals(projectEligibilityForm.getResearchCategoryId());
                    if (researchCategoriesYesNoChanged || researchCategoriesChanged) {
                        competition.setResearchCategories(projectEligibilityForm.getResearchCategoryId());
                        return competitionSetupRestService.update(competition).toServiceResult().andOnSuccess(() ->
                                grantClaimMaximumRestService.revertToDefaultForCompetitionType(competition.getId()));
                    } else {
                        //No form changes. So no need to reset funding levels or update competition.
                        return serviceSuccess();
                    }
                });
    }

    private ServiceResult<Boolean> handleResearchCategoryApplicableChanges(CompetitionResource competitionResource,
                                                                        FundingEligibilityResearchCategoryForm projectEligibilityForm) {

        Optional<QuestionResource> researchCategoryQuestionIfExists = getResearchCategoryQuestionIfExists
                (competitionResource.getId());

        if (projectEligibilityForm.getResearchCategoriesApplicable()) {
            if (!researchCategoryQuestionIfExists.isPresent()) {
                return questionSetupCompetitionRestService.addResearchCategoryQuestionToCompetition(competitionResource
                        .getId())
                        .toServiceResult()
                        .andOnSuccessReturn(() -> true);
            }
        } else {
            if (researchCategoryQuestionIfExists.isPresent()) {
                QuestionResource researchCategoryQuestion = researchCategoryQuestionIfExists.get();
                return questionSetupCompetitionRestService.deleteById(researchCategoryQuestion.getId())
                        .toServiceResult()
                        .andOnSuccessReturn(() -> true);
            }
        }
        return serviceSuccess(false);
    }

    private Optional<QuestionResource> getResearchCategoryQuestionIfExists(long competitionId) {
        RestResult<QuestionResource> researchCategoryQuestionResult = questionRestService
                .getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY);
        return researchCategoryQuestionResult.toOptionalIfNotFound().getSuccess();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return FundingEligibilityResearchCategoryForm.class.equals(clazz);
    }

}
