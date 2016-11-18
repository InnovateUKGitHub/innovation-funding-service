package com.worth.ifs.assessment.resource;

/**
 * Builder for {@link AssessmentFundingDecisionResource}s.
 */
public class AssessmentFundingDecisionResourceBuilder {
    private Boolean fundingConfirmation;
    private String feedback;
    private String comment;

    public AssessmentFundingDecisionResourceBuilder setFundingConfirmation(Boolean fundingConfirmation) {
        this.fundingConfirmation = fundingConfirmation;
        return this;
    }

    public AssessmentFundingDecisionResourceBuilder setFeedback(String feedback) {
        this.feedback = feedback;
        return this;
    }

    public AssessmentFundingDecisionResourceBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public AssessmentFundingDecisionResource createAssessmentFundingDecisionResource() {
        return new AssessmentFundingDecisionResource(fundingConfirmation, feedback, comment);
    }
}