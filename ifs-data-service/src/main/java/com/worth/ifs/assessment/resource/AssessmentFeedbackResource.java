package com.worth.ifs.assessment.resource;

/**
 * Represents feedback given as part of the assessment journey to a question for an application.
 */
public class AssessmentFeedbackResource {

    private Long id;
    private Long processRole;
    private String feedback;
    private Integer score;
    private Long question;

    public AssessmentFeedbackResource() {
    }

    public AssessmentFeedbackResource(Long id, Long processRole, String feedback, Integer score, Long question) {
        this.id = id;
        this.processRole = processRole;
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

    public Long getProcessRole() {
        return processRole;
    }

    public void setProcessRole(Long processRole) {
        this.processRole = processRole;
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
}
