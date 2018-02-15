package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.ActivityState;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_INTERVIEW;

public class AssessmentInterviewBuilder extends BaseBuilder<Interview, AssessmentInterviewBuilder> {

    private AssessmentInterviewBuilder(List<BiConsumer<Integer, Interview>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInterviewBuilder newAssessmentInterview() {
        return new AssessmentInterviewBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentInterviewBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Interview>> actions) {
        return new AssessmentInterviewBuilder(actions);
    }

    @Override
    protected Interview createInitial() {
        return new Interview();
    }

    public AssessmentInterviewBuilder withId(Long... ids) {
        return withArray((id, invite) -> setField("id", id, invite), ids);
    }

    public AssessmentInterviewBuilder withTarget(Application... applications) {
        return withArray((application, invite) -> invite.setTarget(application), applications);
    }

    public AssessmentInterviewBuilder withParticipant(ProcessRole... participants) {
        return withArray((participant, invite) -> invite.setParticipant(participant), participants);
    }

    public AssessmentInterviewBuilder withState(InterviewState... states) {
        return withArray((state, invite) -> invite.setActivityState(new ActivityState(ASSESSMENT_INTERVIEW, state.getBackingState())), states);
    }
}