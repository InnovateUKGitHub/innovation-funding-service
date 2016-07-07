package com.worth.ifs.assessment.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentFeedbackResource that = (AssessmentFeedbackResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(assessment, that.assessment)
                .append(feedback, that.feedback)
                .append(score, that.score)
                .append(question, that.question)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(assessment)
                .append(feedback)
                .append(score)
                .append(question)
                .toHashCode();
    }
}
