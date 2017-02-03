package org.innovateuk.ifs.assessment.resource;

/**
 * Builder for {@link AssessmentFundingDecisionOutcomeResource}s.
 */
public class AssessmentFundingDecisionOutcomeResourceBuilder {
    private Boolean fundingConfirmation;
    private String feedback;
    private String comment;

    public AssessmentFundingDecisionOutcomeResourceBuilder setFundingConfirmation(Boolean fundingConfirmation) {
        this.fundingConfirmation = fundingConfirmation;
        return this;
    }

    public AssessmentFundingDecisionOutcomeResourceBuilder setFeedback(String feedback) {
        this.feedback = feedback;
        return this;
    }

    public AssessmentFundingDecisionOutcomeResourceBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public AssessmentFundingDecisionOutcomeResource createAssessmentFundingDecisionResource() {
        return new AssessmentFundingDecisionOutcomeResource(fundingConfirmation, feedback, comment);
    }
}
