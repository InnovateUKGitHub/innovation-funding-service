package org.innovateuk.ifs.invite.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.util.Optional;

import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;

/**
 * A {@link Participant} in a {@link Competition}.
 */
@Entity
@Table(name = "competition_user")
public class CompetitionAssessmentParticipant extends CompetitionParticipant<CompetitionInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private CompetitionInvite invite;

    @Override
    public CompetitionInvite getInvite() {
        return this.invite;
    }

    public CompetitionAssessmentParticipant() {
        // no-arg constructor
        super.setProcess(null);
    }

    public CompetitionAssessmentParticipant(CompetitionInvite invite) {
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
        super.setRole(CompetitionParticipantRole.ASSESSOR);
    }

    private CompetitionAssessmentParticipant accept() {
        if (getUser() == null) {
            throw new IllegalStateException("Illegal attempt to accept a CompetitionAssessmentParticipant with no User");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a CompetitionAssessmentParticipant that hasn't been opened");
        }

        if (getStatus() == REJECTED) {
            throw new IllegalStateException("Cannot accept a CompetitionAssessmentParticipant that has been rejected");
        }

        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("CompetitionAssessmentParticipant has already been accepted");
        }

        super.setStatus(ACCEPTED);

        return this;
    }

    public void setStatus(ParticipantStatus status) {
        super.setStatus(status);
    }

    public void setRole(CompetitionParticipantRole role) {
        super.setRole(role);
    }

    public CompetitionAssessmentParticipant acceptAndAssignUser(User user) {
        super.setUser(user);
        return accept();
    }

    public CompetitionAssessmentParticipant reject(RejectionReason rejectionReason, Optional<String> rejectionComment) {
        if (rejectionReason == null) {
            throw new NullPointerException("rejectionReason cannot be null");
        }
        if (rejectionComment == null) {
            throw new NullPointerException("rejectionComment cannot be null");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a CompetitionInvite that hasn't been opened");
        }
        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("Cannot reject a CompetitionInvite that has been accepted");
        }
        if (getStatus() == REJECTED) {
            throw new IllegalStateException("CompetitionInvite has already been rejected");
        }

        super.setRejectionReason(rejectionReason);
        super.setRejectionReasonComment(rejectionComment.orElse(null));
        super.setStatus(REJECTED);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionAssessmentParticipant that = (CompetitionAssessmentParticipant) o;

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
