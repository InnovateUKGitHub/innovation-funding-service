package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AssessmentFeedbackResourceBuilder extends BaseBuilder<AssessmentFeedbackResource, AssessmentFeedbackResourceBuilder> {

    private AssessmentFeedbackResourceBuilder(final List<BiConsumer<Integer, AssessmentFeedbackResource>> newActions) {
        super(newActions);
    }

    public static AssessmentFeedbackResourceBuilder newAssessmentFeedbackResource() {
        return new AssessmentFeedbackResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentFeedbackResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentFeedbackResource>> actions) {
        return new AssessmentFeedbackResourceBuilder(actions);
    }

    @Override
    protected AssessmentFeedbackResource createInitial() {
        return new AssessmentFeedbackResource();
    }

    public AssessmentFeedbackResourceBuilder withId(final Long... ids) {
        return withArray(BuilderAmendFunctions::setId, ids);
    }

    public AssessmentFeedbackResourceBuilder withAssessment(final Long... assessments) {
        return withArray((assessment, assessmentFeedback) -> setField("assessment", assessment, assessmentFeedback), assessments);
    }

    public AssessmentFeedbackResourceBuilder withFeedback(final String... feedbacks) {
        return withArray((feedback, assessmentFeedback) -> setField("feedback", feedback, assessmentFeedback), feedbacks);
    }

    public AssessmentFeedbackResourceBuilder withScore(final Integer... scores) {
        return withArray((score, assessmentFeedback) -> setField("score", score, assessmentFeedback), scores);
    }

    public AssessmentFeedbackResourceBuilder withQuestion(final Long... questions) {
        return withArray((question, assessmentFeedback) -> setField("question", question, assessmentFeedback), questions);
    }

}
