package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;


public class InterviewAssessorAllocateApplicationsResourceBuilder
        extends BaseBuilder<InterviewAssessorAllocateApplicationsResource, InterviewAssessorAllocateApplicationsResourceBuilder> {

    private InterviewAssessorAllocateApplicationsResourceBuilder(List<BiConsumer<Integer, InterviewAssessorAllocateApplicationsResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewAssessorAllocateApplicationsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssessorAllocateApplicationsResource>> actions) {
        return new InterviewAssessorAllocateApplicationsResourceBuilder(actions);
    }

    @Override
    protected InterviewAssessorAllocateApplicationsResource createInitial() {
        return new InterviewAssessorAllocateApplicationsResource();
    }

    public static InterviewAssessorAllocateApplicationsResourceBuilder newInterviewAssessorAllocateApplicationsResource() {
        return new InterviewAssessorAllocateApplicationsResourceBuilder(emptyList());
    }

    public InterviewAssessorAllocateApplicationsResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public InterviewAssessorAllocateApplicationsResourceBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public InterviewAssessorAllocateApplicationsResourceBuilder withSkillArears(String... value) {
        return withArraySetFieldByReflection("skillAreas", value);
    }

}