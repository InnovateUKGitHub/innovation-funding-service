package org.innovateuk.ifs.application.areas.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.populator.AbstractLeadOnlyModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategorySummaryModelPopulator extends AbstractLeadOnlyModelPopulator {

    public ApplicationResearchCategorySummaryModelPopulator(ApplicantRestService applicantRestService,
                                                            QuestionRestService questionRestService) {
        super(applicantRestService, questionRestService);
    }

    public ResearchCategorySummaryViewModel populate(ApplicationResource applicationResource,
                                                     long loggedInUserId,
                                                     boolean userIsLeadApplicant) {
        String researchCategoryName = Optional.of(applicationResource.getResearchCategory())
                .map(ResearchCategoryResource::getName).orElse(null);

        return new ResearchCategorySummaryViewModel(applicationResource.getId(),
                getResearchCategoryTeamQuestion(applicationResource.getCompetition()),
                researchCategoryName,
                !isCompetitionOpen(applicationResource),
                isComplete(applicationResource, loggedInUserId),
                userIsLeadApplicant
        );
    }

    private long getResearchCategoryTeamQuestion(long competitionId) {
        return questionRestService.getQuestionByCompetitionIdAndCompetitionSetupQuestionType(competitionId,
                RESEARCH_CATEGORY).getSuccess().getId();
    }
}
