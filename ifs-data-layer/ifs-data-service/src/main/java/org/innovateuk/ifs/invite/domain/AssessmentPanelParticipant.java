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
public class AssessmentPanelParticipant extends Participant<Competition, AssessmentPanelInvite, CompetitionParticipantRole> {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", referencedColumnName = "id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private AssessmentPanelInvite invite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejection_reason_id")
    private RejectionReason rejectionReason;

    @Column(name = "rejection_comment")
    private String rejectionReasonComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "competition_role")
    private CompetitionParticipantRole role;

    public AssessmentPanelParticipant() {
        // no-arg constructor
        this.competition = null;
    }

    public AssessmentPanelParticipant(AssessmentPanelInvite invite) {
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

        this.user = invite.getUser();
        this.competition = invite.getTarget();
        this.invite = invite;
        this.role = CompetitionParticipantRole.ASSESSOR;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Competition getProcess() {
        return competition;
    }

    public void setProcess(Competition process) {
        this.competition = process;
    }

    @Override
    public AssessmentPanelInvite getInvite() {
        return invite;
    }

    @Override
    public CompetitionParticipantRole getRole() {
        return role;
    }

    public void setRole(CompetitionParticipantRole role) {
        this.role = role;
    }

    @Override
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new NullPointerException("user cannot be null");
        }
        if (this.user != null && !this.user.getId().equals(user.getId())) {
            throw new IllegalStateException("Illegal attempt to reassign CompetitionParticipant.user");
        }
        this.user = user;
    }

    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public String getRejectionReasonComment() {
        return rejectionReasonComment;
    }

    private AssessmentPanelParticipant accept() {
        if (user == null) {
            throw new IllegalStateException("Illegal attempt to accept a CompetitionParticipant with no User");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a CompetitionParticipant that hasn't been opened");
        }

        if (getStatus() == REJECTED) {
            throw new IllegalStateException("Cannot accept a CompetitionParticipant that has been rejected");
        }

        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("CompetitionParticipant has already been accepted");
        }

        super.setStatus(ACCEPTED);

        return this;
    }

    public void setStatus(ParticipantStatus status) {
        super.setStatus(status);
    }

    public AssessmentPanelParticipant acceptAndAssignUser(User user) {
        setUser(user);
        return accept();
    }

    public AssessmentPanelParticipant reject(RejectionReason rejectionReason, Optional<String> rejectionComment) {
        if (rejectionReason == null) {
            throw new NullPointerException("rejectionReason cannot be null");
        }
        if (rejectionComment == null) {
            throw new NullPointerException("rejectionComment cannot be null");
        }

        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a CompetitionParticipant that hasn't been opened");
        }
        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("Cannot reject a CompetitionParticipant that has been accepted");
        }
        if (getStatus() == REJECTED) {
            throw new IllegalStateException("CompetitionParticipant has already been rejected");
        }

        this.rejectionReason = rejectionReason;
        this.rejectionReasonComment = rejectionComment.orElse(null);
        setStatus(REJECTED);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentPanelParticipant that = (AssessmentPanelParticipant) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(competition, that.competition)
                .append(user, that.user)
                .append(invite, that.invite)
                .append(rejectionReason, that.rejectionReason)
                .append(rejectionReasonComment, that.rejectionReasonComment)
                .append(role, that.role)
                .append(getStatus(), that.getStatus())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(competition)
                .append(user)
                .append(invite)
                .append(rejectionReason)
                .append(rejectionReasonComment)
                .append(role)
                .append(getStatus())
                .toHashCode();
    }
}
