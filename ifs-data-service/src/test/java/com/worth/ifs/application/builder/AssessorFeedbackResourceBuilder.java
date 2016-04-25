package com.worth.ifs.application.builder;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.user.domain.ProcessRole;

import static com.worth.ifs.BuilderAmendFunctions.getId;
import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AssessorFeedbackResourceBuilder extends BaseBuilder<AssessorFeedbackResource, AssessorFeedbackResourceBuilder> {

    private AssessorFeedbackResourceBuilder(List<BiConsumer<Integer, AssessorFeedbackResource>> multiActions) {
        super(multiActions);
    }

    public static AssessorFeedbackResourceBuilder newAssessorFeedbackResource() {
        return new AssessorFeedbackResourceBuilder(emptyList()).
                with(uniqueIds()).
                withFeedback((i, feedback) -> "Feedback text " + getId(feedback)).
                withAssessmentValue((i, feedback) -> "" + getId(feedback));
    }

    @Override
    protected AssessorFeedbackResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorFeedbackResource>> actions) {
        return new AssessorFeedbackResourceBuilder(actions);
    }

    @Override
    protected AssessorFeedbackResource createInitial() {
        return new AssessorFeedbackResource();
    }

    public AssessorFeedbackResourceBuilder withFeedback(String feedbackText) {
        return withFeedback((i, f) -> feedbackText);
    }

    public AssessorFeedbackResourceBuilder withFeedback(BiFunction<Integer, AssessorFeedbackResource, String> feedbackText) {
        return with((i, feedback) -> feedback.setAssessmentFeedback(feedbackText.apply(i, feedback)));
    }

    public AssessorFeedbackResourceBuilder withAssessmentValue(String value) {
        return withAssessmentValue((i, f) -> value);
    }

    public AssessorFeedbackResourceBuilder withAssessmentValue(BiFunction<Integer, AssessorFeedbackResource, String> value) {
        return with((i, feedback) -> feedback.setAssessmentValue(value.apply(i, feedback)));
    }

    public AssessorFeedbackResourceBuilder withAssessor(ProcessRole assessorProcessRole) {
        return with(feedback -> {
            setField("assessor", assessorProcessRole.getId(), feedback);
        });
    }

    public AssessorFeedbackResourceBuilder withId(Long... ids) {
        return withArray((id, address) -> setField("id", id, address), ids);
    }

    public AssessorFeedbackResourceBuilder withResponse(Long... responses) {
        return withArray((response, address) -> setField("response", response, address), responses);
    }

    public AssessorFeedbackResourceBuilder withAssessor(Long... assessors) {
        return withArray((assessor, address) -> setField("assessor", assessor, address), assessors);
    }

}
