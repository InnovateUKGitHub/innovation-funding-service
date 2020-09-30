package org.innovateuk.ifs.cofunder.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * {@code Assessment} is the state of a review that takes place for an Application by an assessor. This activity happens
 * once the application has been submitted and the competition reaches the Assessment phase. It is also associated
 * with the assessor responses provided during the assessment.
 */
@Entity
public class CofunderAssignment extends Process<User, Application, CofunderState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private User participant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private CofunderOutcome cofunderOutcome;

    @Column(name="activity_state_id")
    private CofunderState activityState;

    public CofunderAssignment() {
        super();
    }

    public CofunderAssignment(Application application, User user) {
        this.participant = user;
        this.target = application;
        this.activityState = CofunderState.CREATED;
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
    public CofunderState getProcessState() {
        return activityState;
    }

    @Override
    public void setProcessState(CofunderState status) {
        this.activityState = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CofunderAssignment that = (CofunderAssignment) o;

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