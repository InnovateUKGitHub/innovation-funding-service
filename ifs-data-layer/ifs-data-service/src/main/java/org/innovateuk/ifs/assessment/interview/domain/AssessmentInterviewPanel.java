package org.innovateuk.ifs.assessment.interview.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;


// TODO Who is the participant? Lead assessor -- probably as they submit responses

@Entity
public class AssessmentInterviewPanel extends Process<ProcessRole, Application, AssessmentInterviewPanelState> {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private AssessmentInterviewPanelMessageOutcome invite;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private AssessmentInterviewPanelResponseOutcome response;

    @Override
    public void setParticipant(ProcessRole participant) {
        this.participant = participant;
    }

    @Override
    public ProcessRole getParticipant() {
        return participant;
    }

    @Override
    public void setTarget(Application target) {
        this.target  = target;
    }

    @Override
    public Application getTarget() {
        return target;
    }

    @Override
    public AssessmentInterviewPanelState getActivityState() {
        return AssessmentInterviewPanelState.fromState(activityState.getState());
    }
}