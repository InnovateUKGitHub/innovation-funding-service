package org.innovateuk.ifs.interview.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;

/**
 * A {@link InterviewParticipant} in a {@link Competition}.
 */
@Entity
@Table(name = "competition_user")
public class InterviewParticipant extends CompetitionParticipant<InterviewInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private InterviewInvite invite;

    @Override
    public InterviewInvite getInvite() {
        return this.invite;
    }

    public InterviewParticipant() {
        super.setProcess(null);
    }

    public InterviewParticipant(InterviewInvite invite) {
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

    private InterviewParticipant accept() {
        if (getUser() == null) {
            throw new IllegalStateException("Illegal attempt to accept an InterviewInvite without a User");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept an InterviewInvite that hasn't been opened");
        }

        if (getStatus() == REJECTED) {
            throw new IllegalStateException("Cannot accept an InterviewInvite that has been rejected");
        }

        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("InterviewInvite has already been accepted");
        }

        super.setStatus(ACCEPTED);

        return this;
    }

    public void setStatus(ParticipantStatus status) {
        super.setStatus(status);
    }

    public InterviewParticipant acceptAndAssignUser(User user) {
        super.setUser(user);
        return accept();
    }

    public InterviewParticipant reject() {
        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept an InterviewInvite that hasn't been opened");
        }
        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("Cannot reject an InterviewInvite that has been accepted");
        }
        if (getStatus() == REJECTED) {
            throw new IllegalStateException("InterviewInvite has already been rejected");
        }

        super.setStatus(REJECTED);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewParticipant that = (InterviewParticipant) o;

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