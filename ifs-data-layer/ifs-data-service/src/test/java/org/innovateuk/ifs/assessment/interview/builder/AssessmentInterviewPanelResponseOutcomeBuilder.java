package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentResponseOutcome;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentInterviewPanelResponseOutcomeBuilder extends BaseBuilder<InterviewAssignmentResponseOutcome, AssessmentInterviewPanelResponseOutcomeBuilder> {

    private AssessmentInterviewPanelResponseOutcomeBuilder(List<BiConsumer<Integer, InterviewAssignmentResponseOutcome>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInterviewPanelResponseOutcomeBuilder newAssessmentInterviewPanelResponseOutcome() {
        return new AssessmentInterviewPanelResponseOutcomeBuilder(emptyList());
    }

    @Override
    protected AssessmentInterviewPanelResponseOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssignmentResponseOutcome>> actions) {
        return new AssessmentInterviewPanelResponseOutcomeBuilder(actions);
    }

    @Override
    protected InterviewAssignmentResponseOutcome createInitial() {
        return new InterviewAssignmentResponseOutcome();
    }

    public AssessmentInterviewPanelResponseOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentInterviewPanelResponseOutcomeBuilder withResponse(String... responses) {
        return withArray((response, assessmentInterviewPanelResponseOutcome) -> assessmentInterviewPanelResponseOutcome.setResponse(response), responses);
    }
}