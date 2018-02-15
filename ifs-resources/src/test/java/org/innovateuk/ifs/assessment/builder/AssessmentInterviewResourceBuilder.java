package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.interview.resource.InterviewState;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AssessmentInterviewResourceBuilder extends BaseBuilder<InterviewResource, AssessmentInterviewResourceBuilder> {

    private AssessmentInterviewResourceBuilder(List<BiConsumer<Integer, InterviewResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInterviewResourceBuilder newAssessmentInterviewResource() {
        return new AssessmentInterviewResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentInterviewResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewResource>> actions) {
        return new AssessmentInterviewResourceBuilder(actions);
    }

    @Override
    protected InterviewResource createInitial() {
        return new InterviewResource();
    }

    public AssessmentInterviewResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public AssessmentInterviewResourceBuilder withStartDate(LocalDate... value) {
        return withArraySetFieldByReflection("startDate", value);
    }

    public AssessmentInterviewResourceBuilder withEndDate(LocalDate... value) {
        return withArraySetFieldByReflection("endDate", value);
    }

    public AssessmentInterviewResourceBuilder withFundingDecision(AssessmentFundingDecisionOutcomeResource... value) {
        return withArraySetFieldByReflection("fundingDecision", value);
    }

    public AssessmentInterviewResourceBuilder withProcessRole(Long... value) {
        return withArraySetFieldByReflection("processRole", value);
    }

    public AssessmentInterviewResourceBuilder withApplication(Long... value) {
        return withArraySetFieldByReflection("application", value);
    }

    public AssessmentInterviewResourceBuilder withApplicationName(String... values) {
        return withArraySetFieldByReflection("applicationName", values);
    }

    public AssessmentInterviewResourceBuilder withCompetition(Long... value) {
        return withArraySetFieldByReflection("competition", value);
    }

    public AssessmentInterviewResourceBuilder withActivityState(InterviewState... value) {
        return withArraySetFieldByReflection("assessmentInterviewState", value);
    }
}
