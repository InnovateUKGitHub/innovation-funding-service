package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.AssessorFeedback;

/**
 * Created by dwatson on 07/10/15.
 */
public class AssessmentSummarySectionQuestionFeedback {

    private String feedbackText;
    private String feedbackValue;

    public AssessmentSummarySectionQuestionFeedback(String feedbackText, String feedbackValue) {
        this.feedbackText = feedbackText;
        this.feedbackValue = feedbackValue;
    }

    public AssessmentSummarySectionQuestionFeedback(AssessorFeedback feedback) {
        this(feedback.getAssessmentFeedback(), feedback.getAssessmentValue());
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public String getFeedbackValue() {
        return feedbackValue;
    }
}
