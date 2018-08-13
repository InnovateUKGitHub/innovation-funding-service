package org.innovateuk.ifs.application.forms.researchcategory.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.researchcategory.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.innovateuk.ifs.competition.service.CompetitionResearchCategoryRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategorySummaryModelPopulator extends AbstractLeadOnlyModelPopulator {

    private QuestionRestService questionRestService;
    private CompetitionResearchCategoryRestService competitionResearchCategoryRestService;

    public ApplicationResearchCategorySummaryModelPopulator(ApplicantRestService applicantRestService,
                                                            QuestionRestService questionRestService,
                                                            CompetitionResearchCategoryRestService competitionResearchCategoryRestService) {
        super(applicantRestService, questionRestService);
        this.questionRestService = questionRestService;
        this.competitionResearchCategoryRestService = competitionResearchCategoryRestService;
    }

    public ResearchCategorySummaryViewModel populate(ApplicationResource applicationResource,
                                                     long loggedInUserId,
                                                     boolean userIsLeadApplicant) {
        String researchCategoryName = Optional.of(applicationResource.getResearchCategory())
                .map(ResearchCategoryResource::getName).orElse(null);

        boolean isComplete = isComplete(applicationResource, loggedInUserId);
        boolean allReadOnly = !userIsLeadApplicant || isComplete;

        return new ResearchCategorySummaryViewModel(applicationResource.getId(),
                getResearchCategoryQuestion(applicationResource.getCompetition()),
                researchCategoryName,
                useSelectedState(applicationResource.getCompetition()),
                isApplicationSubmitted(applicationResource) || !isCompetitionOpen(applicationResource),
                isComplete,
                userIsLeadApplicant,
                allReadOnly
        );
    }

    private Long getResearchCategoryQuestion(long competitionId) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId,
                RESEARCH_CATEGORY).handleSuccessOrFailure(failure -> null, QuestionResource::getId);
    }

    private boolean useSelectedState(long competitionId) {
        List<CompetitionResearchCategoryLinkResource> researchCategories = competitionResearchCategoryRestService.findByCompetition(competitionId)
                .handleSuccessOrFailure(failure -> emptyList(), success -> success);
        return researchCategories.size() > 1;
    }
}
