package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentFeedbackAggregateResourceBuilder extends BaseBuilder<AssessmentFeedbackAggregateResource, AssessmentFeedbackAggregateResourceBuilder> {

    private AssessmentFeedbackAggregateResourceBuilder(List<BiConsumer<Integer, AssessmentFeedbackAggregateResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentFeedbackAggregateResourceBuilder newAssessmentFeedbackAggregateResource() {
        return new AssessmentFeedbackAggregateResourceBuilder(emptyList());
    }

    @Override
    protected AssessmentFeedbackAggregateResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentFeedbackAggregateResource>> actions) {
        return new AssessmentFeedbackAggregateResourceBuilder(actions);
    }

    @Override
    protected AssessmentFeedbackAggregateResource createInitial() {
        return new AssessmentFeedbackAggregateResource();
    }

    public AssessmentFeedbackAggregateResourceBuilder withAvgScore(BigDecimal... value) {
        return withArraySetFieldByReflection("avgScore", value);
    }

    public AssessmentFeedbackAggregateResourceBuilder withFeedback(List<String>... value) {
        return withArraySetFieldByReflection("feedback", value);
    }

}
