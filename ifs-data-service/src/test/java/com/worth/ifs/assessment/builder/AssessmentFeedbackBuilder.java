package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.assessment.domain.AssessmentFeedback;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Builder for {@link com.worth.ifs.assessment.domain.AssessmentFeedback}
 */
public class AssessmentFeedbackBuilder extends BaseBuilder<AssessmentFeedback, AssessmentFeedbackBuilder> {

    private AssessmentFeedbackBuilder(final List<BiConsumer<Integer, AssessmentFeedback>> newActions) {
        super(newActions);
    }

    @Override
    protected AssessmentFeedbackBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentFeedback>> actions) {
        return new AssessmentFeedbackBuilder(actions);
    }

    @Override
    protected AssessmentFeedback createInitial() {
        return new AssessmentFeedback();
    }
}