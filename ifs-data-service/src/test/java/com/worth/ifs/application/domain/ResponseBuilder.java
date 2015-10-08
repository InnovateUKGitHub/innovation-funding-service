package com.worth.ifs.application.domain;

import com.worth.ifs.user.domain.ProcessRole;

import java.util.function.Consumer;

import static com.worth.ifs.user.domain.ProcessRoleBuilder.newProcessRole;

/**
 * Created by dwatson on 08/10/15.
 */
public class ResponseBuilder implements Builder<Response> {

    private final Response current;

    private ResponseBuilder(Response newValue) {
        // uninstantiable
        this.current = newValue;
    }

    public static ResponseBuilder newResponse() {
        return new ResponseBuilder(new Response());
    }

    @Override
    public ResponseBuilder with(Consumer<Response> amendFunction) {
        Response newValue = new Response(current);
        amendFunction.accept(newValue);
        return new ResponseBuilder(newValue);
    }

    @Override
    public Response build() {
        return current;
    }

    public ResponseBuilder withId(Long id) {
        return with(response -> response.setId(id));
    }

    public ResponseBuilder withValue(String value) {
        return with(response -> response.setValue(value));
    }

    public ResponseBuilder withUpdatedBy(Builder<ProcessRole> value) {
        return with(response -> response.setUpdatedBy(value.build()));
    }

    public static void main(String[] args) {

        newResponse().
                withId(1L).
                withValue("New value").
                withUpdatedBy(newProcessRole()).
                build();
    }


}
