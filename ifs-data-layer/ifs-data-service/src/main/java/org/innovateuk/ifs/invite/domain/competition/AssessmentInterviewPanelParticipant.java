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
 * A {@link AssessmentInterviewPanelParticipant} in a {@link Competition}.
 */
@Entity
@Table(name = "competition_user")
public class AssessmentInterviewPanelParticipant extends CompetitionParticipant<AssessmentInterviewPanelInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private AssessmentInterviewPanelInvite invite;

    @Override
    public AssessmentInterviewPanelInvite getInvite() {
        return this.invite;
    }

    public AssessmentInterviewPanelParticipant() {
        super.setProcess(null);
    }

    public AssessmentInterviewPanelParticipant(AssessmentInterviewPanelInvite invite) {
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
        super.setRole(CompetitionParticipantRole.INTERVIEW_ASSESSOR);
    }

    private AssessmentInterviewPanelParticipant accept() {
        if (getUser() == null) {
            throw new IllegalStateException("Illegal attempt to accept a AssessmentInterviewPanelInvite with no User");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a AssessmentInterviewPanelInvite that hasn't been opened");
        }

        if (getStatus() == REJECTED) {
            throw new IllegalStateException("Cannot accept a AssessmentInterviewPanelInvite that has been rejected");
        }

        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("AssessmentInterviewPanelInvite has already been accepted");
        }

        super.setStatus(ACCEPTED);

        return this;
    }

    public void setStatus(ParticipantStatus status) {
        super.setStatus(status);
    }

    public AssessmentInterviewPanelParticipant acceptAndAssignUser(User user) {
        super.setUser(user);
        return accept();
    }

    public AssessmentInterviewPanelParticipant reject() {
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

        AssessmentInterviewPanelParticipant that = (AssessmentInterviewPanelParticipant) o;

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