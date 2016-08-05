package com.worth.ifs.competition.domain;


import com.worth.ifs.invite.domain.CompetitionAssessorInvite;
import com.worth.ifs.user.domain.User;

import javax.persistence.*;

import static com.worth.ifs.competition.domain.CompetitionAssessorStatus.ACCEPTED;
import static com.worth.ifs.competition.domain.CompetitionAssessorStatus.REJECTED;

/**
 * An Assessor for a {@link Competition}
 */
@Entity
@Table(name = "competition_user")
public class CompetitionAssessor {

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
    private CompetitionAssessorInvite invite;

    @Enumerated(EnumType.STRING)
    private CompetitionAssessorStatus status;

    @ManyToOne
    @JoinColumn(name = "rejection_reason_id", insertable = false, updatable = false)
    private CompetitionAssessorRejectionReason rejectionReason;

    @Column(name = "rejection_comment")
    private String rejectionReasonComment;

    CompetitionAssessor() {
        // no-arg constructor
    }

    public CompetitionAssessor(Competition competition, User user) {
        this(competition, user, null);
        if (user == null) throw new NullPointerException("user cannot be null");
    }

    public CompetitionAssessor(Competition competition, CompetitionAssessorInvite invite) {
        this(competition, null, invite);
        if (invite == null) throw new NullPointerException("invite cannot be null");
    }

    private CompetitionAssessor(Competition competition, User user, CompetitionAssessorInvite invite) {
        if (competition == null) throw new NullPointerException("competition cannot be null");

        this.competition = competition;
        this.user = user;
        this.status = CompetitionAssessorStatus.PENDING;
    }

    public Competition getCompetition() {
        return competition;
    }

    public CompetitionAssessorInvite getCompetitionAssessorInvite() {
        return invite;
    }

    public CompetitionAssessorStatus getCompetitionAssessorStatus() {
        return status;
    }

    public CompetitionAssessorRejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public String getRejectionReasonComment() {
        return rejectionReasonComment;
    }

    public void accept() {
        if (status == REJECTED)
            throw new IllegalStateException("Cannot accept a CompetitionAssessor that has been rejected");
        if (status == ACCEPTED)
            throw new IllegalStateException("CompetitionAssessor has already been accepted");

        this.status = ACCEPTED;
    }

    public void reject(CompetitionAssessorRejectionReason rejectionReason, String comment) {
        if (rejectionReason == null) throw new NullPointerException("reason cannot be null");
        if (comment == null) throw new NullPointerException("comment cannot be null");

        if (status == ACCEPTED)
            throw new IllegalStateException("Cannot reject a CompetitionAssessor that has been accepted");
        if (status == REJECTED)
            throw new IllegalStateException("CompetitionAssessor has already been rejected");

        this.rejectionReason = rejectionReason;
        this.rejectionReasonComment = comment;
        this.status = REJECTED;
    }
}
