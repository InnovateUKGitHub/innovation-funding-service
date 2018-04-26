package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.util.Lists.emptyList;

public class InterviewAssignmentApplicationPageResourceBuilder
        extends PageResourceBuilder<InterviewAssignmentApplicationPageResource, InterviewAssignmentApplicationPageResourceBuilder, InterviewAssignmentApplicationResource> {

    public static InterviewAssignmentApplicationPageResourceBuilder newInterviewAssignmentApplicationPageResource() {
        return new InterviewAssignmentApplicationPageResourceBuilder(emptyList());
    }

    public InterviewAssignmentApplicationPageResourceBuilder(List<BiConsumer<Integer, InterviewAssignmentApplicationPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewAssignmentApplicationPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssignmentApplicationPageResource>> actions) {
        return new InterviewAssignmentApplicationPageResourceBuilder(actions);
    }

    @Override
    protected InterviewAssignmentApplicationPageResource createInitial() {
        return new InterviewAssignmentApplicationPageResource();
    }
}