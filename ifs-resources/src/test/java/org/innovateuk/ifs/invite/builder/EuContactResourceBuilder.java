package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;
import org.innovateuk.ifs.invite.resource.EuContactResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuContactResourceBuilder extends BaseBuilder<EuContactResource, EuContactResourceBuilder> {

    private EuContactResourceBuilder(List<BiConsumer<Integer, EuContactResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected EuContactResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuContactResource>> actions) {
        return new EuContactResourceBuilder(actions);
    }

    @Override
    protected EuContactResource createInitial() {
        return new EuContactResource();
    }

    public static EuContactResourceBuilder newEuContactResource() {
        return new EuContactResourceBuilder(emptyList());
    }

    public EuContactResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public EuContactResourceBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public EuContactResourceBuilder withEmail(String... value) {
        return withArraySetFieldByReflection("email", value);
    }

    public EuContactResourceBuilder withTelephone(String... value) {
        return withArraySetFieldByReflection("telephone", value);
    }

    public EuContactResourceBuilder withJobTitle(String... value) {
        return withArraySetFieldByReflection("jobTitle", value);
    }

    public EuContactResourceBuilder withNotified(Boolean... value) {
        return withArraySetFieldByReflection("notified", value);
    }
}
