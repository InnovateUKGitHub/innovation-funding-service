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

    public AssessmentFundingDecisionOutcomeResourceBuilder withFundingConfirmation(Boolean... values) {
        return withArray((value, object) -> object.setFundingConfirmation(value), values);
    }

    public AssessmentFundingDecisionOutcomeResourceBuilder withComment(String... values) {
        return withArray((value, object) -> object.setComment(value), values);
    }

    public AssessmentFundingDecisionOutcomeResourceBuilder withFeedback(String... values) {
        return withArray((value, object) -> object.setFeedback(value), values);
    }
}
