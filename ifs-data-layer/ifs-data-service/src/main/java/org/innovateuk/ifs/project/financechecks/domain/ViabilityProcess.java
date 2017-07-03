package org.innovateuk.ifs.project.financechecks.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    // for ORM use
    ViabilityProcess() {
    }

    public ViabilityProcess(ProjectUser participant, PartnerOrganisation target, ActivityState originalState) {
        this.participant = participant;
        this.target = target;
        this.setActivityState(originalState);
    }

    public ViabilityProcess(User internalParticipant, PartnerOrganisation target, ActivityState originalState) {
        this.internalParticipant = internalParticipant;
        this.target = target;
        this.setActivityState(originalState);
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
    public ViabilityState getActivityState() {
        return ViabilityState.fromState(activityState.getState());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ViabilityProcess that = (ViabilityProcess) o;

        return new EqualsBuilder()
                .append(participant, that.participant)
                .append(internalParticipant, that.internalParticipant)
                .append(target, that.target)
                .append(activityState, that.activityState)
                .append(getProcessEvent(), that.getProcessEvent())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(participant)
                .append(target)
                .toHashCode();
    }
}
