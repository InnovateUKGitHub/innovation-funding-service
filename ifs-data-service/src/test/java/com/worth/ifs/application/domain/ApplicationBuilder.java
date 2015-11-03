package com.worth.ifs.application.domain;

import com.worth.ifs.BaseBuilder;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class ApplicationBuilder<ApplicationBuilder> extends BaseBuilder<Application, Y> {

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

    public ApplicationBuilder withId(Long... ids) {
        return with((id, application) -> application.setId(id), ids);
    }
}
