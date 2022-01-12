package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInvitePageResource;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorCreatedInvitePageResourceBuilder
        extends PageResourceBuilder<AssessorCreatedInvitePageResource, AssessorCreatedInvitePageResourceBuilder, AssessorCreatedInviteResource> {

    public static AssessorCreatedInvitePageResourceBuilder newAssessorCreatedInvitePageResource() {
        return new AssessorCreatedInvitePageResourceBuilder(emptyList());
    }

    public AssessorCreatedInvitePageResourceBuilder(List<BiConsumer<Integer, AssessorCreatedInvitePageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AssessorCreatedInvitePageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorCreatedInvitePageResource>> actions) {
        return new AssessorCreatedInvitePageResourceBuilder(actions);
    }

    @Override
    protected AssessorCreatedInvitePageResource createInitial() {
        return new AssessorCreatedInvitePageResource();
    }
}