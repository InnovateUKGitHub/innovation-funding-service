package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ApplicationStatusResourceBuilder extends BaseBuilder<ApplicationStatusResource, ApplicationStatusResourceBuilder> {

    private ApplicationStatusResourceBuilder(List<BiConsumer<Integer, ApplicationStatusResource>> multiActions) {
        super(multiActions);
    }

    public static ApplicationStatusResourceBuilder newApplicationStatusResource() {
        return new ApplicationStatusResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationStatusResource>> actions) {
        return new ApplicationStatusResourceBuilder(actions);
    }

    @Override
    protected ApplicationStatusResource createInitial() {
        return new ApplicationStatusResource();
    }

    public ApplicationStatusResourceBuilder withName(ApplicationStatusConstants... names) {
        return withArray((name, applicationStatus) -> applicationStatus.setName(name.getName()), names);
    }

    public ApplicationStatusResourceBuilder withName(String... names) {
        return withArray((name, applicationStatus) -> applicationStatus.setName(name), names);
    }
}
