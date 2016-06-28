package com.worth.ifs.assessment.resource;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents feedback given as part of the assessment journey to a question for an application.
 */
public class AssessmentFeedbackResource {

    private Long id;
    private Long assessment;
    private String feedback;
    private Integer score;
    private Long question;

    public AssessmentFeedbackResource() {
    }

    public AssessmentFeedbackResource(Long id, Long assessment, String feedback, Integer score, Long question) {
        this.id = id;
        this.assessment = assessment;
        this.feedback = feedback;
        this.score = score;
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssessment() {
        return assessment;
    }

    public void setAssessment(Long assessment) {
        this.assessment = assessment;
    }

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

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }

    public boolean isComplete() {
        return score != null && StringUtils.isNotBlank(feedback);
    }
}
