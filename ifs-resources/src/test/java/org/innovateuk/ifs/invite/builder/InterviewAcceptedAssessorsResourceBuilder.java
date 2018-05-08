package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;


public class InterviewAcceptedAssessorsResourceBuilder
        extends BaseBuilder<InterviewAcceptedAssessorsResource, InterviewAcceptedAssessorsResourceBuilder> {

    private InterviewAcceptedAssessorsResourceBuilder(List<BiConsumer<Integer, InterviewAcceptedAssessorsResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewAcceptedAssessorsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAcceptedAssessorsResource>> actions) {
        return new InterviewAcceptedAssessorsResourceBuilder(actions);
    }

    @Override
    protected InterviewAcceptedAssessorsResource createInitial() {
        return new InterviewAcceptedAssessorsResource();
    }

    public static InterviewAcceptedAssessorsResourceBuilder newInterviewAssessorAllocateApplicationsResource() {
        return new InterviewAcceptedAssessorsResourceBuilder(emptyList());
    }

    public InterviewAcceptedAssessorsResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public InterviewAcceptedAssessorsResourceBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public InterviewAcceptedAssessorsResourceBuilder withSkillArears(String... value) {
        return withArraySetFieldByReflection("skillAreas", value);
    }

}