package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class InterviewAssessorAllocateApplicationsPageResourceBuilder extends PageResourceBuilder<InterviewAssessorAllocateApplicationsPageResource, InterviewAssessorAllocateApplicationsPageResourceBuilder, InterviewAssessorAllocateApplicationsResource> {

    public static InterviewAssessorAllocateApplicationsPageResourceBuilder newInterviewAssessorAllocateApplicationsPageResource() {
        return new InterviewAssessorAllocateApplicationsPageResourceBuilder(emptyList());
    }

    public InterviewAssessorAllocateApplicationsPageResourceBuilder(List<BiConsumer<Integer, InterviewAssessorAllocateApplicationsPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewAssessorAllocateApplicationsPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssessorAllocateApplicationsPageResource>> actions) {
        return new InterviewAssessorAllocateApplicationsPageResourceBuilder(actions);
    }

    @Override
    protected InterviewAssessorAllocateApplicationsPageResource createInitial() {
        return new InterviewAssessorAllocateApplicationsPageResource();
    }
}