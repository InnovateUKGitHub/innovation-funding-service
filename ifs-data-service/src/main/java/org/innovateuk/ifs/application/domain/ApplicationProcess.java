package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ApplicationProcess extends Process<ProcessRole, Application, ApplicationState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @Deprecated
    public static ApplicationProcess fromApplicationStatus(Application application, ApplicationStatus applicationStatus) {
        return new ApplicationProcess(application, null, applicationStatus.toApplicationState());
    }

    ApplicationProcess() {
    }

    public ApplicationProcess(Application target, ProcessRole participant, ActivityState initialState) {
        this.target = target;
        this.participant = participant;
        this.setActivityState(initialState);
    }

    public ApplicationProcess(Application target, ProcessRole participant) {
        this.target = target;
        this.participant = participant;
        this.setActivityState(new ActivityState(ActivityType.APPLICATION, ApplicationState.CREATED.getBackingState()));
    }

    // this is really for tests / builders
    public ApplicationProcess(Application target, ProcessRole participant, ApplicationState initialState) {
        this.target = target;
        this.participant = participant;
        this.setActivityState(new ActivityState(ActivityType.APPLICATION, initialState.getBackingState()));
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
