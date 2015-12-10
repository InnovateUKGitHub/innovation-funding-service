package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.user.domain.ProcessRole;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static com.worth.ifs.BuilderAmendFunctions.getId;
import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AssessorFeedbackBuilder extends BaseBuilder<AssessorFeedback, AssessorFeedbackBuilder> {

    private AssessorFeedbackBuilder(List<BiConsumer<Integer, AssessorFeedback>> multiActions) {
        super(multiActions);
    }

    public static AssessorFeedbackBuilder newFeedback() {
        return new AssessorFeedbackBuilder(emptyList()).
                with(uniqueIds()).
                withFeedback((i, feedback) -> "Feedback text " + getId(feedback)).
                withAssessmentValue((i, feedback) -> "" + getId(feedback));
    }

    @Override
    protected AssessorFeedbackBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorFeedback>> actions) {
        return new AssessorFeedbackBuilder(actions);
    }

    @Override
    protected AssessorFeedback createInitial() {
        return new AssessorFeedback();
    }

    public AssessorFeedbackBuilder withFeedback(String feedbackText) {
        return withFeedback((i, f) -> feedbackText);
    }

    public AssessorFeedbackBuilder withFeedback(BiFunction<Integer, AssessorFeedback, String> feedbackText) {
        return with((i, feedback) -> feedback.setAssessmentFeedback(feedbackText.apply(i, feedback)));
    }

    public AssessorFeedbackBuilder withAssessmentValue(String value) {
        return withAssessmentValue((i, f) -> value);
    }

    public AssessorFeedbackBuilder withAssessmentValue(BiFunction<Integer, AssessorFeedback, String> value) {
        return with((i, feedback) -> feedback.setAssessmentValue(value.apply(i, feedback)));
    }

    public AssessorFeedbackBuilder withAssessor(ProcessRole assessorProcessRole) {
        return with(feedback -> {
            setField("assessor", assessorProcessRole, feedback);
            setField("assessorId", assessorProcessRole.getId(), feedback);
        });
    }
}
