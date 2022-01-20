package org.innovateuk.ifs.interview.builder;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;


public class InterviewAssignmentStagedApplicationPageResourceBuilder
        extends PageResourceBuilder<InterviewAssignmentStagedApplicationPageResource, InterviewAssignmentStagedApplicationPageResourceBuilder, InterviewAssignmentStagedApplicationResource> {

    public static InterviewAssignmentStagedApplicationPageResourceBuilder newInterviewAssignmentStagedApplicationPageResource() {
        return new InterviewAssignmentStagedApplicationPageResourceBuilder(ImmutableList.of());
    }

    public InterviewAssignmentStagedApplicationPageResourceBuilder(List<BiConsumer<Integer, InterviewAssignmentStagedApplicationPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewAssignmentStagedApplicationPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssignmentStagedApplicationPageResource>> actions) {
        return new InterviewAssignmentStagedApplicationPageResourceBuilder(actions);
    }

    @Override
    protected InterviewAssignmentStagedApplicationPageResource createInitial() {
        return new InterviewAssignmentStagedApplicationPageResource();
    }
}