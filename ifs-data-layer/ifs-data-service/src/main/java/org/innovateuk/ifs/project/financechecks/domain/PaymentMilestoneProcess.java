package org.innovateuk.ifs.project.financechecks.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * The process of approving Payment milestones for an {@link org.innovateuk.ifs.organisation.domain.Organisation}
 */
@Entity
public class PaymentMilestoneProcess extends Process<ProjectUser, PartnerOrganisation, PaymentMilestoneState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProjectUser participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private PartnerOrganisation target;

    @Column(name="activity_state_id")
    private PaymentMilestoneState activityState;

    PaymentMilestoneProcess() {
    }

    public PaymentMilestoneProcess(ProjectUser participant, PartnerOrganisation target, PaymentMilestoneState originalState) {
        this.participant = participant;
        this.target = target;
        this.setProcessState(originalState);
    }

    public PaymentMilestoneProcess(User internalParticipant, PartnerOrganisation target, PaymentMilestoneState originalState) {
        this.internalParticipant = internalParticipant;
        this.target = target;
        this.setProcessState(originalState);
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
    public PaymentMilestoneState getProcessState() {
        return activityState;
    }

    @Override
    public void setProcessState(PaymentMilestoneState status) {
        this.activityState = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PaymentMilestoneProcess that = (PaymentMilestoneProcess) o;

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
