package com.worth.ifs.assessment.viewmodel;

/**
 * Container of the combined feebback data and question data of a same question.
 */
public class QuestionWithFeedbackHelper{

    private Long questionId;
    private String questionShortName;

    private String questionNumber;
    private String assessmentFeedback;
    private Integer assessmentScore;

    public QuestionWithFeedbackHelper(Long id, String questionShortName,String questionNumber,String feedback, Integer score) {
        this.questionId = id;
        this.questionShortName =questionShortName;
        this.questionNumber = questionNumber;
        this.assessmentFeedback = feedback;
        this.assessmentScore = score;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getQuestionShortName() {
        return questionShortName;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public String getAssessmentFeedback() {
        return assessmentFeedback;
    }

    public Integer getAssessmentScore() {
        return assessmentScore;
    }
}
