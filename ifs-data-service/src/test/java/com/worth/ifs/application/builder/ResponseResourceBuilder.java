package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.ResponseResource;
import com.worth.ifs.user.resource.ProcessRoleResource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ResponseResourceBuilder extends BaseBuilder<ResponseResource, ResponseResourceBuilder> {

    private ResponseResourceBuilder(List<BiConsumer<Integer, ResponseResource>> amendActions) {
        super(amendActions);
    }

    public static ResponseResourceBuilder newResponseResource() {
        return new ResponseResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ResponseResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ResponseResource>> actions) {
        return new ResponseResourceBuilder(actions);
    }

    @Override
    protected ResponseResource createInitial() {
        return new ResponseResource();
    }

    public ResponseResourceBuilder withId(Long... ids) {
        return withArray((id, response) -> setField("id", id, response), ids);
    }

    public ResponseResourceBuilder withApplication(Builder<ApplicationResource, ?> application) {
        return withApplication(application.build());
    }

    public ResponseResourceBuilder withApplication(ApplicationResource... applications) {
        return withArray((application, response) -> response.setApplication(application.getId()), applications);
    }

    public ResponseResourceBuilder withUpdatedBy(Builder<ProcessRoleResource, ?> value) {
        return withUpdatedBy(value.build().getId());
    }

    public ResponseResourceBuilder withUpdatedBy(Long updatedBy) {
        return with(response -> response.setUpdatedBy(updatedBy));
    }

    public ResponseResourceBuilder withQuestion(Builder<QuestionResource, ?> question) {
        return with(response -> response.setQuestion(question.build().getId()));
    }

    public ResponseResourceBuilder withQuestion(QuestionResource question) {
        return with(response -> response.setQuestion(question.getId()));
    }

    public ResponseResourceBuilder withFeedback(List<AssessorFeedbackResource> feedbacks) {
        return withList(feedbacks, (feedback, response) -> {
            List<Long> existingFeedback = response.getResponseAssessmentFeedbacks();
            List<Long> newFeedback = new ArrayList<>();
            newFeedback.addAll(existingFeedback);
            newFeedback.add(feedback.getId());
            setField("responseAssessmentFeedbacks", newFeedback, response);

            // add an ORM-style back ref as Hibernate does
            setField("response", response.getId(), feedback);
        });
    }
}
