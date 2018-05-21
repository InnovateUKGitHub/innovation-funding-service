package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class InterviewAcceptedAssessorsPageResourceBuilder extends PageResourceBuilder<InterviewAcceptedAssessorsPageResource, InterviewAcceptedAssessorsPageResourceBuilder, InterviewAcceptedAssessorsResource> {

    public static InterviewAcceptedAssessorsPageResourceBuilder newInterviewAcceptedAssessorsPageResource() {
        return new InterviewAcceptedAssessorsPageResourceBuilder(emptyList());
    }

    public InterviewAcceptedAssessorsPageResourceBuilder(List<BiConsumer<Integer, InterviewAcceptedAssessorsPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewAcceptedAssessorsPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAcceptedAssessorsPageResource>> actions) {
        return new InterviewAcceptedAssessorsPageResourceBuilder(actions);
    }

    @Override
    protected InterviewAcceptedAssessorsPageResource createInitial() {
        return new InterviewAcceptedAssessorsPageResource();
    }
}