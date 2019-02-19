package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;
import org.innovateuk.ifs.invite.resource.EuContactResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuContactPageResourceBuilder extends PageResourceBuilder<EuContactPageResource, EuContactPageResourceBuilder, EuContactResource> {
    public static EuContactPageResourceBuilder newEuContactPageResource() {
        return new EuContactPageResourceBuilder(emptyList());
    }

    public EuContactPageResourceBuilder(List<BiConsumer<Integer, EuContactPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected EuContactPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuContactPageResource>> actions) {
        return new EuContactPageResourceBuilder(actions);
    }

    @Override
    protected EuContactPageResource createInitial() {
        return new EuContactPageResource();
    }
}

