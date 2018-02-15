package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentInterviewPanelMessageOutcomeBuilder extends BaseBuilder<InterviewAssignmentMessageOutcome, AssessmentInterviewPanelMessageOutcomeBuilder> {

    private AssessmentInterviewPanelMessageOutcomeBuilder(List<BiConsumer<Integer, InterviewAssignmentMessageOutcome>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInterviewPanelMessageOutcomeBuilder newAssessmentInterviewPanelMessageOutcome() {
        return new AssessmentInterviewPanelMessageOutcomeBuilder(emptyList());
    }

    @Override
    protected AssessmentInterviewPanelMessageOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssignmentMessageOutcome>> actions) {
        return new AssessmentInterviewPanelMessageOutcomeBuilder(actions);
    }

    @Override
    protected InterviewAssignmentMessageOutcome createInitial() {
        return new InterviewAssignmentMessageOutcome();
    }

    public AssessmentInterviewPanelMessageOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentInterviewPanelMessageOutcomeBuilder withSubject(String... subjects) {
        return withArray((subject, assessmentInterviewPanelMessageOutcome) -> assessmentInterviewPanelMessageOutcome.setMessage(subject), subjects);
    }

    public AssessmentInterviewPanelMessageOutcomeBuilder withMessage(String... messages) {
        return withArray((message, assessmentInterviewPanelMessageOutcome) -> assessmentInterviewPanelMessageOutcome.setMessage(message), messages);
    }
}