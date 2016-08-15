package com.worth.ifs.competition.domain;


import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.Participant;
import com.worth.ifs.user.domain.User;

import javax.persistence.*;

import static com.worth.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static com.worth.ifs.invite.domain.ParticipantStatus.REJECTED;

/**
 * An Assessor for a {@link Competition}
 */
@Entity
@Table(name = "competition_user")
public class CompetitionParticipant extends Participant<Competition, CompetitionInvite> {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "competition", insertable = false, updatable = false)
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "user", insertable = false, updatable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "invite")
    private CompetitionInvite invite;

    @ManyToOne
    @JoinColumn(name = "rejection_reason_id", insertable = false, updatable = false)
    private CompetitionParticipantRejectionReason rejectionReason;

    @Column(name = "rejection_comment")
    private String rejectionReasonComment;

    CompetitionParticipant() {
        // no-arg constructor
    }

    public CompetitionParticipant(Competition competition, User user) {
        this(competition, user, null);
        if (user == null) throw new NullPointerException("user cannot be null");
    }

    public CompetitionParticipant(Competition competition, CompetitionInvite invite) {
        this(competition, null, invite);
        if (invite == null) throw new NullPointerException("invite cannot be null");
    }

    private CompetitionParticipant(Competition competition, User user, CompetitionInvite invite) {
        super();
        if (competition == null) throw new NullPointerException("competition cannot be null");

        this.competition = competition;
        this.user = user;
        this.invite = invite;
    }

    @Override
    public Competition getProcess() {
        return competition;
    }

    @Override
    public CompetitionInvite getInvite() {
        return invite;
    }

    public CompetitionParticipantRejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public String getRejectionReasonComment() {
        return rejectionReasonComment;
    }

    public void accept() {
        if (getStatus() == REJECTED)
            throw new IllegalStateException("Cannot accept a CompetitionParticipant that has been rejected");
        if (getStatus() == ACCEPTED)
            throw new IllegalStateException("CompetitionParticipant has already been accepted");

        setStatus(ACCEPTED);
    }

    public void reject(CompetitionParticipantRejectionReason rejectionReason, String comment) {
        if (rejectionReason == null) throw new NullPointerException("reason cannot be null");
        if (comment == null) throw new NullPointerException("comment cannot be null");

        if (getStatus() == ACCEPTED)
            throw new IllegalStateException("Cannot reject a CompetitionParticipant that has been accepted");
        if (getStatus() == REJECTED)
            throw new IllegalStateException("CompetitionParticipant has already been rejected");

        this.rejectionReason = rejectionReason;
        this.rejectionReasonComment = comment;
        setStatus(REJECTED);
    }
}
