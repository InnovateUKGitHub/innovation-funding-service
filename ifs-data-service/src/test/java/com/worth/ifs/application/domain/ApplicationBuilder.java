package com.worth.ifs.application.domain;

import com.worth.ifs.BaseBuilder;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class ApplicationBuilder extends BaseBuilder<Application> {

    private ApplicationBuilder() {
        super();
    }

    private ApplicationBuilder(List<BiConsumer<Integer, Application>> multiActions) {
        super(multiActions);
    }

    public static ApplicationBuilder newApplication() {
        return new ApplicationBuilder();
    }

    @Override
    protected ApplicationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Application>> actions) {
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
