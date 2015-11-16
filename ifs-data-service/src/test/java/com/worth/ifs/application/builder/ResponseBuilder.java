package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.user.domain.ProcessRole;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class ResponseBuilder extends BaseBuilder<Response, ResponseBuilder> {

    private ResponseBuilder(List<BiConsumer<Integer, Response>> amendActions) {
        super(amendActions);
    }

    public static ResponseBuilder newResponse() {
        return new ResponseBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ResponseBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Response>> actions) {
        return new ResponseBuilder(actions);
    }

    @Override
    protected Response createInitial() {
        return new Response();
    }

    public ResponseBuilder withId(Long id) {
        return with(response -> id(id));
    }

    public ResponseBuilder withApplication(Builder<Application, ?> application) {
        return withApplication(application.build());
    }

    public ResponseBuilder withApplication(Application... applications) {
        return with((application, response) -> response.setApplication(application), applications);
    }

    public ResponseBuilder withUpdatedBy(Builder<ProcessRole, ?> value) {
        return with(response -> response.setUpdatedBy(value.build()));
    }

    public ResponseBuilder withQuestion(Builder<Question, ?> question) {
        return with(response -> response.setQuestion(question.build()));
    }

    public ResponseBuilder withQuestions(List<Question> questions) {
        return withList(questions, (question, response) -> {
            response.setQuestion(question);
            // add a back-ref
            List<Response> responses = question.getResponses();
            List<Response> updated = responses != null ? new ArrayList<>(responses) : new ArrayList<>();
            updated.add(response);
            question.setResponses(updated);
        });
    }

    public ResponseBuilder withFeedback(List<AssessorFeedback> feedbacks) {
        return withList(feedbacks, (feedback, response) -> {
            List<AssessorFeedback> existingFeedback = response.getResponseAssessmentFeedbacks();
            List<AssessorFeedback> newFeedback = new ArrayList<>();
            newFeedback.addAll(existingFeedback);
            newFeedback.add(feedback);
            setField("responseAssessmentFeedbacks", newFeedback, response);

            // add an ORM-style back ref as Hibernate does
            setField("response", response, feedback);
        });
    }
}
