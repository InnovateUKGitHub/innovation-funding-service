package org.innovateuk.ifs.invite.domain.competition;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;

/**
 * A {@link AssessmentReviewPanelParticipant} in a {@link Competition}.
 */
@Entity
@Table(name = "competition_user")
public class AssessmentReviewPanelParticipant extends CompetitionParticipant<AssessmentReviewPanelInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private AssessmentReviewPanelInvite invite;

    @Override
    public AssessmentReviewPanelInvite getInvite() {
        return this.invite;
    }

    public AssessmentReviewPanelParticipant() {
        super.setProcess(null);
    }

    public AssessmentReviewPanelParticipant(AssessmentReviewPanelInvite invite) {
        super();
        if (invite == null) {
            throw new NullPointerException("invite cannot be null");
        }

        if (invite.getTarget() == null) {
            throw new NullPointerException("invite.target cannot be null");
        }

        if (invite.getStatus() != SENT && invite.getStatus() != OPENED) {
            throw new IllegalArgumentException("invite.status must be SENT or OPENED");
        }

        if (invite.getUser() != null) {
            super.setUser(invite.getUser());
        }
        super.setProcess(invite.getTarget());
        this.invite = invite;
        super.setRole(CompetitionParticipantRole.PANEL_ASSESSOR);
    }

    private AssessmentReviewPanelParticipant accept() {
        if (getUser() == null) {
            throw new IllegalStateException("Illegal attempt to accept a CompetitionAssessmentParticipant with no User");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a AssessmentReviewPanelInvite that hasn't been opened");
        }

        if (getStatus() == REJECTED) {
            throw new IllegalStateException("Cannot accept a AssessmentReviewPanelParticipant that has been rejected");
        }

        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("AssessmentReviewPanelParticipant has already been accepted");
        }

        super.setStatus(ACCEPTED);

        return this;
    }

    public void setStatus(ParticipantStatus status) {
        super.setStatus(status);
    }

    public AssessmentReviewPanelParticipant acceptAndAssignUser(User user) {
        super.setUser(user);
        return accept();
    }

    public AssessmentReviewPanelParticipant reject() {
        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a CompetitionAssessmentInvite that hasn't been opened");
        }
        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("Cannot reject a CompetitionAssessmentInvite that has been accepted");
        }
        if (getStatus() == REJECTED) {
            throw new IllegalStateException("CompetitionAssessmentInvite has already been rejected");
        }

        super.setStatus(REJECTED);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentReviewPanelParticipant that = (AssessmentReviewPanelParticipant) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(invite, that.invite)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(invite)
                .toHashCode();
    }
}