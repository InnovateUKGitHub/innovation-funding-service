package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanelResponseOutcome;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentInterviewPanelResponseOutcomeBuilder extends BaseBuilder<AssessmentInterviewPanelResponseOutcome, AssessmentInterviewPanelResponseOutcomeBuilder> {

    private AssessmentInterviewPanelResponseOutcomeBuilder(List<BiConsumer<Integer, AssessmentInterviewPanelResponseOutcome>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInterviewPanelResponseOutcomeBuilder newAssessmentInterviewPanelResponseOutcome() {
        return new AssessmentInterviewPanelResponseOutcomeBuilder(emptyList());
    }

    @Override
    protected AssessmentInterviewPanelResponseOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentInterviewPanelResponseOutcome>> actions) {
        return new AssessmentInterviewPanelResponseOutcomeBuilder(actions);
    }

    @Override
    protected AssessmentInterviewPanelResponseOutcome createInitial() {
        return new AssessmentInterviewPanelResponseOutcome();
    }

    public AssessmentInterviewPanelResponseOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentInterviewPanelResponseOutcomeBuilder withResponse(String... responses) {
        return withArray((response, assessmentInterviewPanelResponseOutcome) -> assessmentInterviewPanelResponseOutcome.setResponse(response), responses);
    }
}