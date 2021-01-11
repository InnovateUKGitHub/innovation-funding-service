package org.innovateuk.ifs.management.competition.setup.fundingeligibility.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.fundingeligibility.form.FundingEligibilityResearchCategoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Form populator for the eligibility competition setup section.
 */
@Service
public class FundingEligibiltyPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private QuestionRestService questionRestService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.FUNDING_ELIGIBILITY;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        FundingEligibilityResearchCategoryForm competitionSetupForm = new FundingEligibilityResearchCategoryForm();

        competitionSetupForm.setResearchCategoryId(competitionResource.getResearchCategories());

        competitionSetupForm.setResearchCategoriesApplicable(getResearchCategoriesApplicable(competitionResource,
                competitionSetupForm));

        return competitionSetupForm;
    }

    private Boolean getResearchCategoriesApplicable(CompetitionResource competitionResource,
                                                    FundingEligibilityResearchCategoryForm projectEligibilityForm) {
        RestResult<QuestionResource> researchCategoryQuestionResult = questionRestService
                .getQuestionByCompetitionIdAndQuestionSetupType(competitionResource.getId(), RESEARCH_CATEGORY);
        return researchCategoryQuestionResult.isSuccess();
    }
}
