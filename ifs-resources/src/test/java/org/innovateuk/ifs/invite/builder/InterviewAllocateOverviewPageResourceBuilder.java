package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class InterviewAllocateOverviewPageResourceBuilder extends PageResourceBuilder<InterviewAllocateOverviewPageResource, InterviewAllocateOverviewPageResourceBuilder, InterviewAllocateOverviewResource> {

    public static InterviewAllocateOverviewPageResourceBuilder newInterviewAssessorAllocateApplicationsPageResource() {
        return new InterviewAllocateOverviewPageResourceBuilder(emptyList());
    }

    public InterviewAllocateOverviewPageResourceBuilder(List<BiConsumer<Integer, InterviewAllocateOverviewPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewAllocateOverviewPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAllocateOverviewPageResource>> actions) {
        return new InterviewAllocateOverviewPageResourceBuilder(actions);
    }

    @Override
    protected InterviewAllocateOverviewPageResource createInitial() {
        return new InterviewAllocateOverviewPageResource();
    }
}