package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.interview.resource.InterviewState;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class InterviewResourceBuilder extends BaseBuilder<InterviewResource, InterviewResourceBuilder> {

    private InterviewResourceBuilder(List<BiConsumer<Integer, InterviewResource>> multiActions) {
        super(multiActions);
    }

    public static InterviewResourceBuilder newInterviewResource() {
        return new InterviewResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InterviewResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewResource>> actions) {
        return new InterviewResourceBuilder(actions);
    }

    @Override
    protected InterviewResource createInitial() {
        return new InterviewResource();
    }

    public InterviewResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public InterviewResourceBuilder withStartDate(LocalDate... value) {
        return withArraySetFieldByReflection("startDate", value);
    }

    public InterviewResourceBuilder withEndDate(LocalDate... value) {
        return withArraySetFieldByReflection("endDate", value);
    }

    public InterviewResourceBuilder withFundingDecision(AssessmentFundingDecisionOutcomeResource... value) {
        return withArraySetFieldByReflection("fundingDecision", value);
    }

    public InterviewResourceBuilder withProcessRole(Long... value) {
        return withArraySetFieldByReflection("processRole", value);
    }

    public InterviewResourceBuilder withApplication(Long... value) {
        return withArraySetFieldByReflection("application", value);
    }

    public InterviewResourceBuilder withApplicationName(String... values) {
        return withArraySetFieldByReflection("applicationName", values);
    }

    public InterviewResourceBuilder withCompetition(Long... value) {
        return withArraySetFieldByReflection("competition", value);
    }

    public InterviewResourceBuilder withActivityState(InterviewState... value) {
        return withArraySetFieldByReflection("interviewState", value);
    }
}
