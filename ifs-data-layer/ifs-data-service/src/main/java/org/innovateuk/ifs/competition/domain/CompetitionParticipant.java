package org.innovateuk.ifs.competition.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DiscriminatorOptions;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;

/**
 * A {@link Participant} in a {@link Competition}.
 */
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorOptions(force = true)
@Entity
@Table(name = "competition_user")
public abstract class CompetitionParticipant<I extends Invite<Competition, I>> extends Participant<Competition, CompetitionParticipantRole>
        implements InvitedParticipant<Competition, I, CompetitionParticipantRole> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", referencedColumnName = "id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejection_reason_id")
    private RejectionReason rejectionReason;

    @Column(name = "rejection_comment")
    private String rejectionReasonComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "competition_role")
    private CompetitionParticipantRole role;

    protected CompetitionParticipant() {
        this.competition = null;
    }

    protected CompetitionParticipant(I invite, CompetitionParticipantRole role) {
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

        this.competition = invite.getTarget();
        this.role = role;

        if (invite.getUser() != null) {
            setUser(invite.getUser());
        }
        setProcess(invite.getTarget());
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
    public CompetitionParticipantRole getRole() {
        return role;
    }

    protected void setRole(CompetitionParticipantRole role) {
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

    protected void setRejectionReason(RejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReasonComment() {
        return rejectionReasonComment;
    }

    protected void setRejectionReasonComment(String rejectionReasonComment) {
        this.rejectionReasonComment = rejectionReasonComment;
    }

    @Override
    protected void setStatus(ParticipantStatus status) {
        super.setStatus(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionParticipant that = (CompetitionParticipant) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(competition, that.competition)
                .append(user, that.user)
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
                .append(rejectionReason)
                .append(rejectionReasonComment)
                .append(role)
                .append(getStatus())
                .toHashCode();
    }
}