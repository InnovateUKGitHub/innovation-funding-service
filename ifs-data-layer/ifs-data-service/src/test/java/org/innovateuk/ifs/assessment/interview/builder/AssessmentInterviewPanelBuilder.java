package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.ActivityState;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_INTERVIEW_PANEL;

public class AssessmentInterviewPanelBuilder extends BaseBuilder<InterviewAssignment, AssessmentInterviewPanelBuilder> {

    private AssessmentInterviewPanelBuilder(List<BiConsumer<Integer, InterviewAssignment>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInterviewPanelBuilder newAssessmentInterviewPanel() {
        return new AssessmentInterviewPanelBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentInterviewPanelBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssignment>> actions) {
        return new AssessmentInterviewPanelBuilder(actions);
    }

    @Override
    protected InterviewAssignment createInitial() {
        return new InterviewAssignment();
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

    public AssessmentInterviewPanelBuilder withState(InterviewAssignmentState... states) {
        return withArray((state, invite) -> invite.setActivityState(new ActivityState(ASSESSMENT_INTERVIEW_PANEL, state.getBackingState())), states);
    }
}