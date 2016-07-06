package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentFeedback;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link com.worth.ifs.assessment.domain.AssessmentFeedback}
 */
public class AssessmentFeedbackBuilder extends BaseBuilder<AssessmentFeedback, AssessmentFeedbackBuilder> {

    private AssessmentFeedbackBuilder(final List<BiConsumer<Integer, AssessmentFeedback>> newActions) {
        super(newActions);
    }

    public static AssessmentFeedbackBuilder newAssessmentFeedback() {
        return new AssessmentFeedbackBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentFeedbackBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentFeedback>> actions) {
        return new AssessmentFeedbackBuilder(actions);
    }

    @Override
    protected AssessmentFeedback createInitial() {
        return new AssessmentFeedback();
    }

    public AssessmentFeedbackBuilder withId(final Long... ids) {
        return withArray(BuilderAmendFunctions::setId, ids);
    }

    public AssessmentFeedbackBuilder withAssessment(final Assessment... assessments) {
        return withArray((assessment, assessmentFeedback) -> setField("assessment", assessment, assessmentFeedback), assessments);
    }

    public AssessmentFeedbackBuilder withFeedback(final String... feedbacks) {
        return withArray((feedback, assessmentFeedback) -> setField("feedback", feedback, assessmentFeedback), feedbacks);
    }

    public AssessmentFeedbackBuilder withScore(final Integer... scores) {
        return withArray((score, assessmentFeedback) -> setField("score", score, assessmentFeedback), scores);
    }

    public AssessmentFeedbackBuilder withQuestion(final Question... questions) {
        return withArray((question, assessmentFeedback) -> setField("question", question, assessmentFeedback), questions);
    }
}