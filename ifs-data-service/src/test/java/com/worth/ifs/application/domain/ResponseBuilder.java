package com.worth.ifs.application.domain;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.user.domain.ProcessRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class ResponseBuilder extends BaseBuilder<Response> {

    private ResponseBuilder() {
        super();
    }

    private ResponseBuilder(List<Consumer<Response>> actions) {
        super(actions);
    }

    public static ResponseBuilder newResponse() {
        return new ResponseBuilder();
    }

    @Override
    protected ResponseBuilder createNewBuilderWithActions(List<Consumer<Response>> actions) {
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

    public ResponseBuilder withApplication(Builder<Application> application) {
        return withApplication(application.build());
    }

    public ResponseBuilder withApplication(Application application) {
        return with(response -> response.setApplication(application));
    }

    public ResponseBuilder withUpdatedBy(Builder<ProcessRole> value) {
        return with(response -> response.setUpdatedBy(value.build()));
    }
}
