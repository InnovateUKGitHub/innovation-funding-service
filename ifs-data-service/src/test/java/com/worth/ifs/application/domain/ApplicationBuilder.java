package com.worth.ifs.application.domain;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class ApplicationBuilder extends BaseBuilder<Application> {

    private ApplicationBuilder() {
        super();
    }

    private ApplicationBuilder(List<Consumer<Application>> actions) {
        super(actions);
    }

    public static ApplicationBuilder newApplication() {
        return new ApplicationBuilder();
    }

    @Override
    protected ApplicationBuilder createNewBuilderWithActions(List<Consumer<Application>> actions) {
        return new ApplicationBuilder(actions);
    }

    @Override
    protected Application createInitial() {
        return new Application();
    }

    public ApplicationBuilder withId(Long id) {
        return with(application -> application.setId(id));
    }
}
