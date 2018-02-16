package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AvailableApplicationPageResourceBuilder
        extends PageResourceBuilder<AvailableApplicationPageResource, AvailableApplicationPageResourceBuilder, AvailableApplicationResource> {

    public static AvailableApplicationPageResourceBuilder newAvailableApplicationPageResource() {
        return new AvailableApplicationPageResourceBuilder(emptyList());
    }

    public AvailableApplicationPageResourceBuilder(List<BiConsumer<Integer, AvailableApplicationPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AvailableApplicationPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AvailableApplicationPageResource>> actions) {
        return new AvailableApplicationPageResourceBuilder(actions);
    }

    @Override
    protected AvailableApplicationPageResource createInitial() {
        return new AvailableApplicationPageResource();
    }
}
