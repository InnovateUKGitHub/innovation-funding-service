package org.innovateuk.ifs.assessment.panel.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.innovateuk.ifs.invite.domain.competition.AssessmentPanelParticipant;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * An invitation for an assessor to review an application on an assessment panel.
 */
@Entity
public class AssessmentReview extends Process<ProcessRole, Application, AssessmentReviewState> {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private AssessmentReviewRejectOutcome rejection;

    public AssessmentReview() {
        super();
    }

    public AssessmentReview(Application application, AssessmentPanelParticipant assessmentPanelParticipant, Role panelAssessorRole) {
        if (!panelAssessorRole.isOfType(UserRoleType.PANEL_ASSESSOR)) {
            throw new IllegalArgumentException("panelAssessorRole must be of type PANEL_ASSESSOR");
        }
        this.participant = new ProcessRole(assessmentPanelParticipant.getUser(), application.getId(), panelAssessorRole);
        this.target = application;
    }

    @Deprecated
    public AssessmentReview(Application application, ProcessRole processRole) {
        if (!application.getId().equals(processRole.getApplicationId())) {
            throw new IllegalArgumentException("application.id must equal processRole.id");
        }
        this.participant = processRole;
        this.target = application;
    }

    public AssessmentReviewRejectOutcome getRejection() {
        return rejection;
    }

    public void setRejection(AssessmentReviewRejectOutcome rejection) {
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

    public AssessmentReviewState getActivityState() {
        return AssessmentReviewState.fromState(activityState.getState());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentReview that = (AssessmentReview) o;

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