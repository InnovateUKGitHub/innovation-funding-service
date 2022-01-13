package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentFundingDecisionOutcomeResourceBuilder
        extends BaseBuilder<AssessmentFundingDecisionOutcomeResource, AssessmentFundingDecisionOutcomeResourceBuilder> {

    private AssessmentFundingDecisionOutcomeResourceBuilder(List<BiConsumer<Integer,
            AssessmentFundingDecisionOutcomeResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentFundingDecisionOutcomeResourceBuilder newAssessmentFundingDecisionOutcomeResource() {
        return new AssessmentFundingDecisionOutcomeResourceBuilder(emptyList());
    }

    @Override
    protected AssessmentFundingDecisionOutcomeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            AssessmentFundingDecisionOutcomeResource>> actions) {
        return new AssessmentFundingDecisionOutcomeResourceBuilder(actions);
    }

    @Override
    protected AssessmentFundingDecisionOutcomeResource createInitial() {
        return new AssessmentFundingDecisionOutcomeResource();
    }

    public AssessmentFundingDecisionOutcomeResourceBuilder withFundingConfirmation(Boolean... fundingConfirmations) {
        return withArray((value, assessmentFundingDecisionOutcomeResource) -> assessmentFundingDecisionOutcomeResource.setFundingConfirmation(value), fundingConfirmations);
    }

    public AssessmentFundingDecisionOutcomeResourceBuilder withComment(String... comments) {
        return withArray((value, assessmentFundingDecisionOutcomeResource) -> assessmentFundingDecisionOutcomeResource.setComment(value), comments);
    }

    public AssessmentFundingDecisionOutcomeResourceBuilder withFeedback(String... feedback) {
        return withArray((value, assessmentFundingDecisionOutcomeResource) -> assessmentFundingDecisionOutcomeResource.setFeedback(value), feedback);
    }
}
