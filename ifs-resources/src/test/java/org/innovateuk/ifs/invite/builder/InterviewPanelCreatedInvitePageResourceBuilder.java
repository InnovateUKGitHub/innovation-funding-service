package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.InterviewPanelCreatedInvitePageResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.util.Lists.emptyList;

public class InterviewPanelCreatedInvitePageResourceBuilder extends PageResourceBuilder<InterviewPanelCreatedInvitePageResource, InterviewPanelCreatedInvitePageResourceBuilder> {

    public static InterviewPanelCreatedInvitePageResourceBuilder newInterviewPanelCreatedInvitePageResource() {
        return new InterviewPanelCreatedInvitePageResourceBuilder(emptyList());
    }

    public InterviewPanelCreatedInvitePageResourceBuilder(List<BiConsumer<Integer, InterviewPanelCreatedInvitePageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewPanelCreatedInvitePageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewPanelCreatedInvitePageResource>> actions) {
        return new InterviewPanelCreatedInvitePageResourceBuilder(actions);
    }

    @Override
    protected InterviewPanelCreatedInvitePageResource createInitial() {
        return new InterviewPanelCreatedInvitePageResource();
    }
}