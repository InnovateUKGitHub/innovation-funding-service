package org.innovateuk.ifs.review.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * An invitation for an assessor to review an application on an assessment panel.
 */
@Entity
public class Review extends Process<ProcessRole, Application, ReviewState> {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private ReviewRejectOutcome rejection;

    public Review() {
        super();
    }

    public Review(Application application, ReviewParticipant reviewParticipant) {
        this.participant = new ProcessRole(reviewParticipant.getUser(), application.getId(), Role.PANEL_ASSESSOR);
        this.target = application;
    }

    @Deprecated
    public Review(Application application, ProcessRole processRole) {
        if (!application.getId().equals(processRole.getApplicationId())) {
            throw new IllegalArgumentException("application.id must equal processRole.id");
        }
        this.participant = processRole;
        this.target = application;
    }

    public ReviewRejectOutcome getRejection() {
        return rejection;
    }

    public void setRejection(ReviewRejectOutcome rejection) {
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

    public ReviewState getActivityState() {
        return ReviewState.fromState(activityState.getState());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Review that = (Review) o;

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