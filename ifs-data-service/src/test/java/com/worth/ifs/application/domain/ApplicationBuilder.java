package com.worth.ifs.application.domain;

import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class ApplicationBuilder implements Builder<Application> {

    private final Application current;

    // for factory method and with() use
    private ApplicationBuilder(Application value) {
        this.current = value;
    }

    public static ApplicationBuilder newApplication() {
        return new ApplicationBuilder(new Application());
    }

    @Override
    public ApplicationBuilder with(Consumer<Application> amendFunction) {
        Application newValue = new Application(current);
        amendFunction.accept(newValue);
        return new ApplicationBuilder(newValue);
    }

    public ApplicationBuilder withId(Long id) {
        return with(application -> application.setId(id));
    }

    @Override
    public Application build() {
        return current;
    }
}
