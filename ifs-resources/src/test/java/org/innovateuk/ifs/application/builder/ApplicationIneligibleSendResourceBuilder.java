package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationIneligibleSendResourceBuilder extends BaseBuilder<ApplicationIneligibleSendResource, ApplicationIneligibleSendResourceBuilder> {

    private ApplicationIneligibleSendResourceBuilder(List<BiConsumer<Integer, ApplicationIneligibleSendResource>> multiActions) {
        super(multiActions);
    }

    public static ApplicationIneligibleSendResourceBuilder newApplicationIneligibleSendResource() {
        return new ApplicationIneligibleSendResourceBuilder(emptyList());
    }

    @Override
    protected ApplicationIneligibleSendResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationIneligibleSendResource>> actions) {
        return new ApplicationIneligibleSendResourceBuilder(actions);
    }

    @Override
    protected ApplicationIneligibleSendResource createInitial() {
        return new ApplicationIneligibleSendResource();
    }

    public ApplicationIneligibleSendResourceBuilder withSubject(String... subject) {
        return withArraySetFieldByReflection("subject", subject);
    }

    public ApplicationIneligibleSendResourceBuilder withContent(String... content) {
        return withArraySetFieldByReflection("content", content);
    }
}
