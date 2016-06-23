package com.worth.ifs.assessment.form;

/**
 * Form field model for the assessment feedback given for a question.
 */
public class AssessmentFeedbackForm {

    private String feedback;
    private Integer score;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
