package org.innovateuk.ifs.application.forms.researchcategory.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

public abstract class AbstractLeadOnlyModelPopulator {

    private ApplicantRestService applicantRestService;
    private QuestionRestService questionRestService;

    public AbstractLeadOnlyModelPopulator(final ApplicantRestService applicantRestService,
                                         final QuestionRestService questionRestService) {
        this.applicantRestService = applicantRestService;
        this.questionRestService = questionRestService;
    }

    protected boolean isCompetitionOpen(ApplicationResource applicationResource) {
        return CompetitionStatus.OPEN == applicationResource.getCompetitionStatus();
    }

    protected boolean isComplete(ApplicationResource applicationResource, long loggedInUserId) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(
                applicationResource.getCompetition(), RESEARCH_CATEGORY).handleSuccessOrFailure(
                failure -> false,
                success -> {
                    ApplicantQuestionResource question = applicantRestService.getQuestion(loggedInUserId,
                            applicationResource.getId(), success.getId());
                    return question.isCompleteByApplicant(question.getCurrentApplicant());
                });
    }
}
