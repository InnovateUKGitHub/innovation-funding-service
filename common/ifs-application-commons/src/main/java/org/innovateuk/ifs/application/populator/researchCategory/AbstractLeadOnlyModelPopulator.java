package org.innovateuk.ifs.application.populator.researchCategory;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.question.resource.QuestionSetupType;

public abstract class AbstractLeadOnlyModelPopulator {

    private ApplicantRestService applicantRestService;
    protected QuestionRestService questionRestService;

    public AbstractLeadOnlyModelPopulator(final ApplicantRestService applicantRestService,
                                         final QuestionRestService questionRestService) {
        this.applicantRestService = applicantRestService;
        this.questionRestService = questionRestService;
    }

    protected boolean isCompetitionOpen(ApplicationResource applicationResource) {
        return CompetitionStatus.OPEN == applicationResource.getCompetitionStatus();
    }

    protected boolean isApplicationSubmitted(ApplicationResource applicationResource) {
        return applicationResource.isSubmitted();
    }

    protected boolean isComplete(ApplicationResource applicationResource, long loggedInUserId, QuestionSetupType questionSetupType) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(
                applicationResource.getCompetition(), questionSetupType).handleSuccessOrFailure(
                failure -> false,
                success -> {
                    ApplicantQuestionResource question = applicantRestService.getQuestion(loggedInUserId,
                            applicationResource.getId(), success.getId());
                    return question.isCompleteByApplicant(question.getCurrentApplicant());
                });
    }
}
