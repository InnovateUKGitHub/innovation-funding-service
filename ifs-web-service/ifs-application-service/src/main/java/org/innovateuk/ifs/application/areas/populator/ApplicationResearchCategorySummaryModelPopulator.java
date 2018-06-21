package org.innovateuk.ifs.application.areas.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.RESEARCH_CATEGORY;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategorySummaryModelPopulator {

    private ApplicantRestService applicantRestService;
    private QuestionRestService questionRestService;

    public ApplicationResearchCategorySummaryModelPopulator(ApplicantRestService applicantRestService,
                                                            QuestionRestService questionRestService) {
        this.applicantRestService = applicantRestService;
        this.questionRestService = questionRestService;
    }

    public ResearchCategorySummaryViewModel populate(ApplicationResource applicationResource, long loggedInUserId,
                                                     boolean userIsLeadApplicant) {
        String researchCategoryName = Optional.of(applicationResource.getResearchCategory())
                .map(ResearchCategoryResource::getName).orElse(null);
        boolean canMarkAsComplete = userIsLeadApplicant;
        boolean closed = !isCompetitionOpen(applicationResource);
        boolean complete = isComplete(applicationResource, loggedInUserId);
        return new ResearchCategorySummaryViewModel(applicationResource.getId(), researchCategoryName,
                canMarkAsComplete, closed, complete);
    }

    private boolean isCompetitionOpen(ApplicationResource applicationResource) {
        return CompetitionStatus.OPEN == applicationResource.getCompetitionStatus();
    }

    private boolean isComplete(ApplicationResource applicationResource, long loggedInUserId) {
        return questionRestService.getQuestionByCompetitionIdAndCompetitionSetupQuestionType(
                applicationResource.getCompetition(), RESEARCH_CATEGORY).handleSuccessOrFailure(
                failure -> false,
                success -> {
                    ApplicantQuestionResource question = applicantRestService.getQuestion(loggedInUserId,
                            applicationResource.getId(), success.getId());
                    return question.isCompleteByApplicant(question.getCurrentApplicant());
                });
    }
}
