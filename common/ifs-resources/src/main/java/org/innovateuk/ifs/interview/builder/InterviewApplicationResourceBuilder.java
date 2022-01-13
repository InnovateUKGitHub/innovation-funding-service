package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;


public class InterviewApplicationResourceBuilder
        extends BaseBuilder<InterviewApplicationResource, InterviewApplicationResourceBuilder> {

    private InterviewApplicationResourceBuilder(List<BiConsumer<Integer, InterviewApplicationResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewApplicationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewApplicationResource>> actions) {
        return new InterviewApplicationResourceBuilder(actions);
    }

    @Override
    protected InterviewApplicationResource createInitial() {
        return new InterviewApplicationResource();
    }

    public static InterviewApplicationResourceBuilder newInterviewApplicationResource() {
        return new InterviewApplicationResourceBuilder(emptyList());
    }

    public InterviewApplicationResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public InterviewApplicationResourceBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public InterviewApplicationResourceBuilder withLeadOrganisation(String... value) {
        return withArraySetFieldByReflection("leadOrganisation", value);
    }

    public InterviewApplicationResourceBuilder withNumberOfAssessors(Long... value) {
        return withArraySetFieldByReflection("numberOfAssessors", value);
    }
}