package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.ActivityState;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_INTERVIEW_PANEL;

public class AssessmentInterviewPanelBuilder extends BaseBuilder<AssessmentInterviewPanel, AssessmentInterviewPanelBuilder> {

    private AssessmentInterviewPanelBuilder(List<BiConsumer<Integer, AssessmentInterviewPanel>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInterviewPanelBuilder newAssessmentInterviewPanel() {
        return new AssessmentInterviewPanelBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentInterviewPanelBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentInterviewPanel>> actions) {
        return new AssessmentInterviewPanelBuilder(actions);
    }

    @Override
    protected AssessmentInterviewPanel createInitial() {
        return new AssessmentInterviewPanel();
    }

    public AssessmentInterviewPanelBuilder withId(Long... ids) {
        return withArray((id, invite) -> setField("id", id, invite), ids);
    }

    public AssessmentInterviewPanelBuilder withTarget(Application... applications) {
        return withArray((application, invite) -> invite.setTarget(application), applications);
    }

    public AssessmentInterviewPanelBuilder withParticipant(ProcessRole... participants) {
        return withArray((participant, invite) -> invite.setParticipant(participant), participants);
    }

    public AssessmentInterviewPanelBuilder withState(AssessmentInterviewPanelState... states) {
        return withArray((state, invite) -> invite.setActivityState(new ActivityState(ASSESSMENT_INTERVIEW_PANEL, state.getBackingState())), states);
    }

    public AssessmentInterviewPanelBuilder withActivityState(ActivityState... states) {
        return withArray((state, invite) -> invite.setActivityState(state), states);
    }
}