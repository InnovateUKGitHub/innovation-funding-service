package org.innovateuk.ifs.application.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.repository.ApplicationStateConverter;
import org.innovateuk.ifs.application.resource.ApplicationState;
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
        this.setProcessState(initialState);
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
    public void setProcessState(ApplicationState status) {
        this.activityState = status;
    }

    public List<IneligibleOutcome> getIneligibleOutcomes() {
        return ineligibleOutcomes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationProcess that = (ApplicationProcess) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(participant, that.participant)
                .append(target, that.target)
                .append(ineligibleOutcomes, that.ineligibleOutcomes)
                .append(activityState, that.activityState)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(participant)
                .append(target)
                .append(ineligibleOutcomes)
                .append(activityState)
                .toHashCode();
    }
}