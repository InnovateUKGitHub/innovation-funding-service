package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.builder.RejectionReasonBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static java.util.Optional.empty;
import static org.junit.Assert.*;

public class CompetitionParticipantTest {

    private CompetitionInvite invite;
    private RejectionReason rejectionReason;

    @Before
    public void setup() {
        invite = newCompetitionInvite()
                .withCompetition( CompetitionBuilder.newCompetition().withName("my competition") )
                .build();
        rejectionReason = RejectionReasonBuilder.newRejectionReason().build();
    }

    @Test
    public void accept() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        User user = newUser().build();

        invite.open();

        competitionParticipant.acceptAndAssignUser(user);
        assertEquals(ParticipantStatus.ACCEPTED, competitionParticipant.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void accept_unopened() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        User user = newUser().build();

        competitionParticipant.acceptAndAssignUser(user);
    }

    @Test(expected = IllegalStateException.class)
    public void accept_alreadyAccepted() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        User user = newUser().build();

        invite.open();

        competitionParticipant.acceptAndAssignUser(user);
        competitionParticipant.acceptAndAssignUser(user);
    }

    @Test(expected = IllegalStateException.class)
    public void accept_alreadyRejected() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        User user = newUser().build();

        invite.open();

        competitionParticipant.reject(rejectionReason, Optional.of("too busy"));
        competitionParticipant.acceptAndAssignUser(user);
    }

    @Test
    public void reject() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, Optional.of("too busy"));
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals(rejectionReason, competitionParticipant.getRejectionReason());
        assertEquals("too busy", competitionParticipant.getRejectionReasonComment());
    }

    @Test(expected = IllegalStateException.class)
    public void reject_unopened() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        competitionParticipant.reject(rejectionReason, Optional.of("too busy"));
    }

    @Test(expected = IllegalStateException.class)
    public void reject_alreadyAccepted() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        User user = newUser().build();

        invite.open();

        competitionParticipant.acceptAndAssignUser(user);
        competitionParticipant.reject(rejectionReason, Optional.of("too busy"));
    }

    @Test(expected = IllegalStateException.class)
    public void reject_alreadyRejected() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, Optional.of("too busy"));
        competitionParticipant.reject(rejectionReason, Optional.of("too busy"));
    }

    @Test(expected = NullPointerException.class)
    public void reject_nullReason() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        invite.open();
        competitionParticipant.reject(null, Optional.of("too busy"));
    }

    @Test(expected = NullPointerException.class)
    public void reject_nullReasonComment() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, null);
    }

    @Test
    public void reject_emptyComment() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, Optional.of(""));
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals(rejectionReason, competitionParticipant.getRejectionReason());
        assertEquals("", competitionParticipant.getRejectionReasonComment());
    }

    @Test
    public void reject_noComment() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, empty());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals(rejectionReason, competitionParticipant.getRejectionReason());
        assertNull(competitionParticipant.getRejectionReasonComment());
    }
}
