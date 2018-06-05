package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentFeedbackResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class ApplicationInterviewSummaryViewModel {
    final static String ASSESSOR_WITH_RESPONSE_BANNER =  "The lead applicant has responded to feedback." +
            " Download and review all attachments before the interview panel.";
    final static String ASSESSOR_WITHOUT_RESPONSE_BANNER =  "The lead applicant can respond to feedback." +
            " This response will be noted by the interview panel.";

    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final String responseFilename;
    private final String feedbackFilename;
    private final ApplicationAssessmentAggregateResource scores;
    private final ApplicationAssessmentFeedbackResource feedback;

    public ApplicationInterviewSummaryViewModel(ApplicationResource application, CompetitionResource competition, String responseFilename, String feedbackFilename, ApplicationAssessmentAggregateResource scores, ApplicationAssessmentFeedbackResource feedback) {
        this.application = application;
        this.competition = competition;
        this.responseFilename = responseFilename;
        this.feedbackFilename = feedbackFilename;
        this.scores = scores;
        this.feedback = feedback;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public String getResponseFilename() {
        return responseFilename;
    }

    public String getFeedbackFilename() {
        return feedbackFilename;
    }

    public ApplicationAssessmentAggregateResource getScores() {
        return scores;
    }

    public ApplicationAssessmentFeedbackResource getFeedback() {
        return feedback;
    }

    /* View logic methods. */
    public boolean hasResponse() {
        return responseFilename != null;
    }

    public boolean hasFeedback() {
        return feedbackFilename != null;
    }

    public String getBannerText() {
        if (hasResponse()) {
            return ASSESSOR_WITH_RESPONSE_BANNER;
        } else {
            return ASSESSOR_WITHOUT_RESPONSE_BANNER;
        }
    }
}
