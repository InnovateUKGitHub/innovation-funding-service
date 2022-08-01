package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentDecisionOutcomeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentDecisionOutcomeResourceBuilder
        extends BaseBuilder<AssessmentDecisionOutcomeResource, AssessmentDecisionOutcomeResourceBuilder> {

    private AssessmentDecisionOutcomeResourceBuilder(List<BiConsumer<Integer,
            AssessmentDecisionOutcomeResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentDecisionOutcomeResourceBuilder newAssessmentDecisionOutcomeResource() {
        return new AssessmentDecisionOutcomeResourceBuilder(emptyList());
    }

    @Override
    protected AssessmentDecisionOutcomeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            AssessmentDecisionOutcomeResource>> actions) {
        return new AssessmentDecisionOutcomeResourceBuilder(actions);
    }

    @Override
    protected AssessmentDecisionOutcomeResource createInitial() {
        return new AssessmentDecisionOutcomeResource();
    }

    public AssessmentDecisionOutcomeResourceBuilder withFundingConfirmation(Boolean... fundingConfirmations) {
        return withArray((value, assessmentDecisionOutcomeResource) -> assessmentDecisionOutcomeResource.setFundingConfirmation(value), fundingConfirmations);
    }

    public AssessmentDecisionOutcomeResourceBuilder withComment(String... comments) {
        return withArray((value, assessmentDecisionOutcomeResource) -> assessmentDecisionOutcomeResource.setComment(value), comments);
    }

    public AssessmentDecisionOutcomeResourceBuilder withFeedback(String... feedback) {
        return withArray((value, assessmentDecisionOutcomeResource) -> assessmentDecisionOutcomeResource.setFeedback(value), feedback);
    }
}
