package org.innovateuk.ifs.supporter.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

@Entity
public class SupporterAssignment extends Process<User, Application, SupporterState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private User participant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY, orphanRemoval = true)
    private SupporterOutcome supporterOutcome;

    @Column(name="activity_state_id")
    private SupporterState activityState;

    public SupporterAssignment() {
        super();
    }

    public SupporterAssignment(Application application, User user) {
        this.participant = user;
        this.target = application;
        this.activityState = SupporterState.CREATED;
    }

    @Override
    public User getParticipant() {
        return participant;
    }

    @Override
    public void setParticipant(User participant) {
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
    public SupporterState getProcessState() {
        return activityState;
    }

    @Override
    public void setProcessState(SupporterState status) {
        this.activityState = status;
    }

    public SupporterOutcome getSupporterOutcome() {
        return supporterOutcome;
    }

    public void setSupporterOutcome(SupporterOutcome supporterOutcome) {
        if (supporterOutcome != null) {
            supporterOutcome.setProcess(this);
        }
        this.supporterOutcome = supporterOutcome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SupporterAssignment that = (SupporterAssignment) o;

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