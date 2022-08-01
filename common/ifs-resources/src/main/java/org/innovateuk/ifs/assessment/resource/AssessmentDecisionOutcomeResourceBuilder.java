package org.innovateuk.ifs.assessment.resource;

/**
 * Builder for {@link AssessmentDecisionOutcomeResource}s.
 */
public class AssessmentDecisionOutcomeResourceBuilder {
    private Boolean fundingConfirmation;
    private String feedback;
    private String comment;

    public AssessmentDecisionOutcomeResourceBuilder setFundingConfirmation(Boolean fundingConfirmation) {
        this.fundingConfirmation = fundingConfirmation;
        return this;
    }

    public AssessmentDecisionOutcomeResourceBuilder setFeedback(String feedback) {
        this.feedback = feedback;
        return this;
    }

    public AssessmentDecisionOutcomeResourceBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public AssessmentDecisionOutcomeResource createAssessmentDecisionResource() {
        return new AssessmentDecisionOutcomeResource(fundingConfirmation, feedback, comment);
    }
}
