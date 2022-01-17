package org.innovateuk.ifs.interview.builder;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;


public class InterviewAssignmentApplicationPageResourceBuilder
        extends PageResourceBuilder<InterviewAssignmentApplicationPageResource, InterviewAssignmentApplicationPageResourceBuilder, InterviewAssignmentApplicationResource> {

    public static InterviewAssignmentApplicationPageResourceBuilder newInterviewAssignmentApplicationPageResource() {
        return new InterviewAssignmentApplicationPageResourceBuilder(ImmutableList.of());
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