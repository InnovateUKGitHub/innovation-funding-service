package org.innovateuk.ifs.project.financechecks.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * The process of approving Viability for Organisations
 */
@Entity
public class ViabilityProcess extends Process<ProjectUser, PartnerOrganisation, ViabilityState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProjectUser participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private PartnerOrganisation target;

    @Column(name="activity_state_id")
    private ViabilityState activityState;

    ViabilityProcess() {
    }

    public ViabilityProcess(ProjectUser participant, PartnerOrganisation target, ViabilityState originalState) {
        this.participant = participant;
        this.target = target;
        this.setActivityState(originalState);
    }

    public ViabilityProcess(User internalParticipant, PartnerOrganisation target, ViabilityState originalState) {
        this.internalParticipant = internalParticipant;
        this.target = target;
        this.setActivityState(originalState);
    }

    @Override
    public void setActivityState(ViabilityState status) {
        this.activityState = status;
    }

    @Override
    public void setParticipant(ProjectUser participant) {
        this.participant = participant;
    }

    @Override
    public ProjectUser getParticipant() {
        return participant;
    }

    @Override
    public PartnerOrganisation getTarget() {
        return target;
    }

    @Override
    public void setTarget(PartnerOrganisation target) {
        this.target = target;
    }

    @Override
    public ViabilityState getProcessState() {
        return activityState;
    }

    @Override
    public ViabilityState getActivityState() {
        return activityState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ViabilityProcess that = (ViabilityProcess) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(participant, that.participant)
                .append(target, that.target)
                .append(activityState, that.activityState)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(participant)
                .append(target)
                .append(activityState)
                .toHashCode();
    }
}