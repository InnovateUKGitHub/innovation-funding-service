package org.innovateuk.ifs.project.financechecks.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.FundingRulesState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

@Entity
public class FundingRulesProcess extends Process<ProjectUser, PartnerOrganisation, FundingRulesState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProjectUser participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private PartnerOrganisation target;

    @Column(name="activity_state_id")
    private FundingRulesState activityState;

    FundingRulesProcess() {
    }

    public FundingRulesProcess(ProjectUser participant, PartnerOrganisation target, FundingRulesState originalState) {
        this.participant = participant;
        this.target = target;
        this.setProcessState(originalState);
    }

    public FundingRulesProcess(User internalParticipant, PartnerOrganisation target, FundingRulesState originalState) {
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
    public FundingRulesState getProcessState() {
        return activityState;
    }

    @Override
    public void setProcessState(FundingRulesState status) {
        this.activityState = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FundingRulesProcess that = (FundingRulesProcess) o;

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
