package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The current state of an {@link Application}.
 */
@Entity
public class ApplicationProcess extends Process<ProcessRole, Application, ApplicationState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    ApplicationProcess() {
    }

    public ApplicationProcess(Application target, ProcessRole participant, ActivityState initialState) {
        this.target = target;
        this.participant = participant;
        this.setActivityState(initialState);
    }

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
        this.target = target;
    }

    @Override
    public Application getTarget() {
        return target;
    }

    @Override
    public ApplicationState getActivityState() {
        return ApplicationState.fromState(activityState.getState());
    }
}
