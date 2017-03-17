package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorInviteOverviewPageResourceBuilder extends PageResourceBuilder<AssessorInviteOverviewPageResource, AssessorInviteOverviewPageResourceBuilder> {

    public static AssessorInviteOverviewPageResourceBuilder newAssessorInviteOverviewPageResource() {
        return new AssessorInviteOverviewPageResourceBuilder(emptyList());
    }

    public AssessorInviteOverviewPageResourceBuilder(List<BiConsumer<Integer, AssessorInviteOverviewPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AssessorInviteOverviewPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorInviteOverviewPageResource>> actions) {
        return new AssessorInviteOverviewPageResourceBuilder(actions);
    }

    @Override
    protected AssessorInviteOverviewPageResource createInitial() {
        return new AssessorInviteOverviewPageResource();
    }
}
