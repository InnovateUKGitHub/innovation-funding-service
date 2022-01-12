package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO for recommending applications for funding during assessment.
 */
@FieldRequiredIf(required = "feedback", argument = "fundingConfirmation", predicate = false, message = "{validation.assessmentFundingDecisionOutcome.feedback.required}")
public class AssessmentFundingDecisionOutcomeResource {

    @NotNull(message = "{validation.assessmentFundingDecisionOutcome.fundingConfirmation.required}")
    private Boolean fundingConfirmation;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String feedback;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String comment;

    public AssessmentFundingDecisionOutcomeResource() {
    }

    public AssessmentFundingDecisionOutcomeResource(Boolean fundingConfirmation, String feedback, String comment) {
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

        AssessmentFundingDecisionOutcomeResource that = (AssessmentFundingDecisionOutcomeResource) o;

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
