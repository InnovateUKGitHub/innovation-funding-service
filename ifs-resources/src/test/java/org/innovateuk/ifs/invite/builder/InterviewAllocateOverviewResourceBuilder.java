package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;


public class InterviewAllocateOverviewResourceBuilder
        extends BaseBuilder<InterviewAllocateOverviewResource, InterviewAllocateOverviewResourceBuilder> {

    private InterviewAllocateOverviewResourceBuilder(List<BiConsumer<Integer, InterviewAllocateOverviewResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewAllocateOverviewResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAllocateOverviewResource>> actions) {
        return new InterviewAllocateOverviewResourceBuilder(actions);
    }

    @Override
    protected InterviewAllocateOverviewResource createInitial() {
        return new InterviewAllocateOverviewResource();
    }

    public static InterviewAllocateOverviewResourceBuilder newInterviewAssessorAllocateApplicationsResource() {
        return new InterviewAllocateOverviewResourceBuilder(emptyList());
    }

    public InterviewAllocateOverviewResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public InterviewAllocateOverviewResourceBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public InterviewAllocateOverviewResourceBuilder withSkillArears(String... value) {
        return withArraySetFieldByReflection("skillAreas", value);
    }

}