package com.worth.ifs.assessment.resource;

import com.worth.ifs.commons.validation.constraints.FieldRequiredIf;
import com.worth.ifs.commons.validation.constraints.WordCount;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO for recommending applications for funding during assessment.
 */
@FieldRequiredIf(required = "feedback", argument = "fundingConfirmation", predicate = false, message = "{validation.assessmentFundingDecision.feedback.required}")
public class AssessmentFundingDecisionResource {

    @NotNull(message = "{validation.assessmentFundingDecision.fundingConfirmation.required}")
    private Boolean fundingConfirmation;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String feedback;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String comment;

    public AssessmentFundingDecisionResource() {
    }

    public AssessmentFundingDecisionResource(Boolean fundingConfirmation, String feedback, String comment) {
        this.fundingConfirmation = fundingConfirmation;
        this.feedback = feedback;
        this.comment = comment;
    }

    public Boolean getFundingConfirmation() {
        return fundingConfirmation;
    }

    public void setFundingConfirmation(Boolean fundingConfirmation) {
        this.fundingConfirmation = fundingConfirmation;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentFundingDecisionResource that = (AssessmentFundingDecisionResource) o;

        return new EqualsBuilder()
                .append(fundingConfirmation, that.fundingConfirmation)
                .append(feedback, that.feedback)
                .append(comment, that.comment)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fundingConfirmation)
                .append(feedback)
                .append(comment)
                .toHashCode();
    }
}