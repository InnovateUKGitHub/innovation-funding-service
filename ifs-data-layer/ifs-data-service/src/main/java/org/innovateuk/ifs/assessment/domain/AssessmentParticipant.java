package org.innovateuk.ifs.assessment.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.util.Optional;

import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;

/**
 * A {@link AssessmentParticipant} in a {@link Competition}.
 */
@Entity
@Table(name = "competition_user")
public class AssessmentParticipant extends CompetitionParticipant<AssessmentInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private AssessmentInvite invite;

    @Override
    public AssessmentInvite getInvite() {
        return this.invite;
    }

    public AssessmentParticipant() {
        super.setProcess(null);
    }

    public AssessmentParticipant(AssessmentInvite invite) {
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

    private AssessmentParticipant accept() {
        if (getUser() == null) {
            throw new IllegalStateException("Illegal attempt to accept a AssessmentParticipant with no User");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a AssessmentInvite that hasn't been opened");
        }

        if (getStatus() == REJECTED) {
            throw new IllegalStateException("Cannot accept a AssessmentParticipant that has been rejected");
        }

        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("AssessmentParticipant has already been accepted");
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

    public AssessmentParticipant acceptAndAssignUser(User user) {
        super.setUser(user);
        return accept();
    }

    public AssessmentParticipant reject(RejectionReason rejectionReason, Optional<String> rejectionComment) {
        if (rejectionReason == null) {
            throw new NullPointerException("rejectionReason cannot be null");
        }
        if (rejectionComment == null) {
            throw new NullPointerException("rejectionComment cannot be null");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a AssessmentInvite that hasn't been opened");
        }
        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("Cannot reject a AssessmentInvite that has been accepted");
        }
        if (getStatus() == REJECTED) {
            throw new IllegalStateException("AssessmentInvite has already been rejected");
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

        AssessmentParticipant that = (AssessmentParticipant) o;

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