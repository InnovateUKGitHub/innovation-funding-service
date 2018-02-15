package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AvailableApplicationResourceBuilder extends BaseBuilder<AvailableApplicationResource, AvailableApplicationResourceBuilder> {

    private AvailableApplicationResourceBuilder(List<BiConsumer<Integer, AvailableApplicationResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AvailableApplicationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AvailableApplicationResource>> actions) {
        return new AvailableApplicationResourceBuilder(actions);
    }

    @Override
    protected AvailableApplicationResource createInitial() {
        return new AvailableApplicationResource();
    }

    public static AvailableApplicationResourceBuilder newAvailableApplicationResource() {
        return new AvailableApplicationResourceBuilder(emptyList());
    }

    public AvailableApplicationResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public AvailableApplicationResourceBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public AvailableApplicationResourceBuilder withLeadOrganisation(String... value) {
        return withArraySetFieldByReflection("leadOrganisation", value);
    }
}