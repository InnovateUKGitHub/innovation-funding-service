package org.innovateuk.ifs.review.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.PANEL_ASSESSOR;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;

/**
 * A {@link ReviewParticipant} in a {@link Competition}.
 */
@Entity
@DiscriminatorValue("REVIEW_PARTICIPANT")
public class ReviewParticipant extends CompetitionParticipant<ReviewInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private ReviewInvite invite;

    @Override
    public ReviewInvite getInvite() {
        return this.invite;
    }

    public ReviewParticipant() {
        super.setProcess(null);
    }

    public ReviewParticipant(ReviewInvite invite) {
        super();

        if (invite.getUser() != null) {
            super.setUser(invite.getUser());
        }
        super.setProcess(invite.getTarget());
        this.invite = invite;
        setRole(PANEL_ASSESSOR);
    }

    private ReviewParticipant accept() {
        if (getUser() == null) {
            throw new IllegalStateException("Illegal attempt to accept a AssessmentParticipant with no User");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a ReviewInvite that hasn't been opened");
        }

        if (getStatus() == REJECTED) {
            throw new IllegalStateException("Cannot accept a ReviewParticipant that has been rejected");
        }

        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("ReviewParticipant has already been accepted");
        }

        super.setStatus(ACCEPTED);
        return this;
    }

    @Override
    public void setStatus(ParticipantStatus status) {
        super.setStatus(status);
    }

    public ReviewParticipant acceptAndAssignUser(User user) {
        super.setUser(user);
        return accept();
    }

    public ReviewParticipant reject() {
        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a AssessmentInvite that hasn't been opened");
        }
        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("Cannot reject a AssessmentInvite that has been accepted");
        }
        if (getStatus() == REJECTED) {
            throw new IllegalStateException("AssessmentInvite has already been rejected");
        }

        super.setStatus(REJECTED);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ReviewParticipant that = (ReviewParticipant) o;

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