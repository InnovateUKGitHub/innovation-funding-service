package org.innovateuk.ifs.assessment.panel.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * TODO class comment
 */
@Entity
public class AssessmentPanelApplicationInvite extends Process<ProcessRole, Application, AssessmentPanelApplicationInviteState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private AssessmentPanelApplicationInviteRejectOutcome rejection;

    public AssessmentPanelApplicationInvite() {
        super();
    }

    public AssessmentPanelApplicationInvite(Application application, ProcessRole processRole) {
        this.participant = processRole;
        this.target = application;
    }

    public AssessmentPanelApplicationInviteRejectOutcome getRejection() {
        return rejection;
    }

    public void setRejection(AssessmentPanelApplicationInviteRejectOutcome rejection) {
        if (rejection != null) {
            rejection.setAssessmentPanelApplicationInvite(this);
        }
        this.rejection = rejection;
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

    public AssessmentPanelApplicationInviteState getActivityState() {
        return AssessmentPanelApplicationInviteState.fromState(activityState.getState());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentPanelApplicationInvite that = (AssessmentPanelApplicationInvite) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(participant, that.participant)
                .append(target, that.target)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(participant)
                .append(target)
                .toHashCode();
    }
}
