package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.repository.ApplicationStateConverter;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name="process_id")
    @OrderBy("id ASC")
    private List<IneligibleOutcome> ineligibleOutcomes = new ArrayList<>();


    @Convert(converter = ApplicationStateConverter.class)
    @Column(name="activity_state_id")
    private ApplicationState activityState;

    ApplicationProcess() {
    }

    public ApplicationProcess(Application target, ProcessRole participant, ApplicationState initialState) {
        this.target = target;
        this.participant = participant;
        this.setActivityState(initialState);
    }

    @Override
    public void setActivityState(ApplicationState status) {
        this.activityState = status;
    }

    @Override
    public ProcessRole getParticipant() {
        return participant;
    }

    @Override
    public void setParticipant(ProcessRole participant) {
        this.participant = participant;
    }

    @Override
    public Application getTarget() {
        return target;
    }

    @Override
    public void setTarget(Application target) {
        this.target = target;
    }

    @Override
    public ApplicationState getProcessState() {
        return activityState;
    }

    @Override
    public ApplicationState getActivityState() { return activityState; }

    public List<IneligibleOutcome> getIneligibleOutcomes() {
        return ineligibleOutcomes;
    }
}