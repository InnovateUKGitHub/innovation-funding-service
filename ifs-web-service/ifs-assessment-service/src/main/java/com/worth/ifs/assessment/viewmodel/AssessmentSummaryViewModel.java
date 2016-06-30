package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;

/**
 * Holder of model attribute of Assessment Summary, which combine the question data and assessment feedback data
 * of a same question
 */
public class AssessmentSummaryViewModel {
    private QuestionResource questionResource;
    private AssessmentFeedbackResource assessmentFeedbackResource;

    public AssessmentSummaryViewModel(QuestionResource questionResource,AssessmentFeedbackResource assessmentFeedbackResource){
        this.questionResource = questionResource;
        this.assessmentFeedbackResource = assessmentFeedbackResource;
    }

    public QuestionResource getQuestionResource(){
        return this.questionResource;
    }
    public AssessmentFeedbackResource getAssessmentFeedbackResource(){
        return this.assessmentFeedbackResource;
    }
}
