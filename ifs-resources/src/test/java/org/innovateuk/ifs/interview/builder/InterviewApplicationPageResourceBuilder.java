package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class InterviewApplicationPageResourceBuilder extends PageResourceBuilder<InterviewApplicationPageResource, InterviewApplicationPageResourceBuilder, InterviewApplicationResource> {

    public static InterviewApplicationPageResourceBuilder newInterviewApplicationPageResource() {
        return new InterviewApplicationPageResourceBuilder(emptyList());
    }

    public InterviewApplicationPageResourceBuilder(List<BiConsumer<Integer, InterviewApplicationPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewApplicationPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewApplicationPageResource>> actions) {
        return new InterviewApplicationPageResourceBuilder(actions);
    }

    @Override
    protected InterviewApplicationPageResource createInitial() {
        return new InterviewApplicationPageResource();
    }

    public InterviewApplicationPageResourceBuilder withUnallocatedApplications(Long... unallocatedApplications) {
        return withArraySetFieldByReflection("unallocatedApplications", unallocatedApplications);
    }

    public InterviewApplicationPageResourceBuilder withAllocatedApplications(Long... allocatedApplications) {
        return withArraySetFieldByReflection("allocatedApplications", allocatedApplications);
    }

}