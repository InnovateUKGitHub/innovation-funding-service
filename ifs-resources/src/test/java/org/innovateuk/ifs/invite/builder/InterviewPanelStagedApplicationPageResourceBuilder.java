package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.util.Lists.emptyList;

public class InterviewPanelStagedApplicationPageResourceBuilder
        extends PageResourceBuilder<InterviewPanelStagedApplicationPageResource, InterviewPanelStagedApplicationPageResourceBuilder, InterviewPanelStagedApplicationResource> {

    public static InterviewPanelStagedApplicationPageResourceBuilder newInterviewPanelStagedApplicationPageResource() {
        return new InterviewPanelStagedApplicationPageResourceBuilder(emptyList());
    }

    public InterviewPanelStagedApplicationPageResourceBuilder(List<BiConsumer<Integer, InterviewPanelStagedApplicationPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewPanelStagedApplicationPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewPanelStagedApplicationPageResource>> actions) {
        return new InterviewPanelStagedApplicationPageResourceBuilder(actions);
    }

    @Override
    protected InterviewPanelStagedApplicationPageResource createInitial() {
        return new InterviewPanelStagedApplicationPageResource();
    }
}