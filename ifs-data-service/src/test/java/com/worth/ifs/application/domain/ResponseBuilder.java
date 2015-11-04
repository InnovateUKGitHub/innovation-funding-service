package com.worth.ifs.application.domain;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.user.domain.ProcessRole;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Created by dwatson on 08/10/15.
 */
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
        return with(response -> response.setId(id));
    }

    public ResponseBuilder withValue(String value) {
        return with(response -> response.setValue(value));
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
        return withList((question, response) -> response.setQuestion(question), questions);
    }
}
