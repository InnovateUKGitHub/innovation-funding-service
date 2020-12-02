package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.fundingeligibility.form.FundingEligibilityResearchCategoryForm;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelPercentageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Form populator for the eligibility competition setup section.
 */
@Service
public class FundingLevelPercentageFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.FUNDING_LEVEL_PERCENTAGE;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        FundingLevelPercentageForm competitionSetupForm = new FundingLevelPercentageForm();

        if (competitionResource.getResearchCategories().isEmpty()) {
            competitionSetupForm.getPercentages().add(getFundingLevelPercentage(competitionResource));
        } else {
            categoryRestService.getResearchCategories().getSuccess();

        }

        competitionSetupForm.setResearchCategoriesApplicable(getResearchCategoriesApplicable(competitionResource,
                competitionSetupForm));

        return competitionSetupForm;
    }
    private Integer getFundingLevelPercentage(CompetitionResource competition) {
        Optional<GrantClaimMaximumResource> grantClaimMaximumResource = competition.getGrantClaimMaximums().stream()
                .findAny().map(this::getGrantClaimMaximumById);
        return grantClaimMaximumResource.map(GrantClaimMaximumResource::getMaximum).orElse(null);
    }

    private GrantClaimMaximumResource getGrantClaimMaximumById(long id) {
        return grantClaimMaximumRestService.getGrantClaimMaximumById(id).getSuccess();
    }

    private Boolean getResearchCategoriesApplicable(CompetitionResource competitionResource,
                                                    FundingEligibilityResearchCategoryForm projectEligibilityForm) {
        RestResult<QuestionResource> researchCategoryQuestionResult = questionRestService
                .getQuestionByCompetitionIdAndQuestionSetupType(competitionResource.getId(), RESEARCH_CATEGORY);
        return researchCategoryQuestionResult.isSuccess();
    }
}
