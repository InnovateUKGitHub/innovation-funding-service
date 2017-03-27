package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.AvailableAssessorPageResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AvailableAssessorPageResourceBuilder extends PageResourceBuilder<AvailableAssessorPageResource, AvailableAssessorPageResourceBuilder> {

    public static AvailableAssessorPageResourceBuilder newAvailableAssessorPageResource() {
        return new AvailableAssessorPageResourceBuilder(emptyList());
    }

    public AvailableAssessorPageResourceBuilder(List<BiConsumer<Integer, AvailableAssessorPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AvailableAssessorPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AvailableAssessorPageResource>> actions) {
        return new AvailableAssessorPageResourceBuilder(actions);
    }

    @Override
    protected AvailableAssessorPageResource createInitial() {
        return new AvailableAssessorPageResource();
    }
}
