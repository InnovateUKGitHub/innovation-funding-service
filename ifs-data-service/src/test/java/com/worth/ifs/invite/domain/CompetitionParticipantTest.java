package com.worth.ifs.invite.domain;

import com.worth.ifs.assessment.builder.CompetitionInviteBuilder;
import com.worth.ifs.competition.builder.CompetitionBuilder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.builder.RejectionReasonBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CompetitionParticipantTest {

    private Competition competition;
    private CompetitionInvite invite;
    private RejectionReason rejectionReason;

    @Before
    public void setup() {
        competition = CompetitionBuilder.newCompetition().withName("my competition").build();
        invite = CompetitionInviteBuilder.newCompetitionInvite().build();
        rejectionReason = RejectionReasonBuilder.newRejectionReason().build();

    }

    @Test
    public void accept() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        invite.open();
        competitionParticipant.accept();
        assertEquals(ParticipantStatus.ACCEPTED, competitionParticipant.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void accept_unopened() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        competitionParticipant.accept();
    }

    @Test(expected = IllegalStateException.class)
    public void accept_alreadyAccepted() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        invite.open();
        competitionParticipant.accept();
        competitionParticipant.accept();
    }

    @Test(expected = IllegalStateException.class)
    public void accept_alreadyRejected() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, "too busy");
        competitionParticipant.accept();
    }

    @Test
    public void reject() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, "too busy");
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals(rejectionReason, competitionParticipant.getRejectionReason());
        assertEquals("too busy", competitionParticipant.getRejectionReasonComment());
    }

    @Test(expected = IllegalStateException.class)
    public void reject_unopened() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        competitionParticipant.reject(rejectionReason, "too busy");
    }

    @Test(expected = IllegalStateException.class)
    public void reject_alreadyAccepted() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        invite.open();
        competitionParticipant.accept();
        competitionParticipant.reject(rejectionReason, "too busy");
    }

    @Test(expected = IllegalStateException.class)
    public void reject_alreadyRejected() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, "too busy");
        competitionParticipant.reject(rejectionReason, "too busy");
    }

    @Test(expected = NullPointerException.class)
    public void reject_nullReason() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        invite.open();
        competitionParticipant.reject(null, "too busy");
    }

    @Test(expected = NullPointerException.class)
    public void reject_nullReasonComment() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void reject_emptyComment() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(competition, invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, "");
    }
}