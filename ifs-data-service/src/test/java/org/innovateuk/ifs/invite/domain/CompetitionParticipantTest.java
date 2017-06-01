package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.invite.builder.RejectionReasonBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class CompetitionParticipantTest {

    private CompetitionInvite invite;
    private RejectionReason rejectionReason;

    @Before
    public void setup() {
        invite = newCompetitionInvite()
                .withCompetition( newCompetition().withName("my competition") )
                .withStatus(SENT)
                .build();
        rejectionReason = RejectionReasonBuilder.newRejectionReason().build();
    }

    @Test
    public void constructor() throws Exception {
        User user = newUser().build();
        CompetitionInvite createdInvite = newCompetitionInvite()
                .withCompetition(newCompetition().withName("my competition"))
                .withStatus(SENT)
                .withUser(user)
                .build();

        CompetitionParticipant competitionParticipant = new CompetitionParticipant(createdInvite);
        assertSame(createdInvite.getUser(), competitionParticipant.getUser());
        assertSame(createdInvite.getTarget(), competitionParticipant.getProcess());
        assertSame(createdInvite, competitionParticipant.getInvite());
        assertSame(ASSESSOR, competitionParticipant.getRole());
        assertSame(PENDING, competitionParticipant.getStatus());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_inviteNull() throws Exception {
        CompetitionInvite competitionInvite = null;
        new CompetitionParticipant(competitionInvite);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_inviteWithoutCompetition() throws Exception {
        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withStatus(SENT)
                .build();
        new CompetitionParticipant(competitionInvite);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_inviteNotSentOrOpened() throws Exception {
        CompetitionInvite createdInvite = newCompetitionInvite()
                .withCompetition(newCompetition().withName("my competition"))
                .withStatus(CREATED)
                .build();

        new CompetitionParticipant(createdInvite);
    }

    @Test
    public void accept() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        User user = newUser().build();

        invite.open();

        competitionParticipant.acceptAndAssignUser(user);
        assertEquals(ACCEPTED, competitionParticipant.getStatus());
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
        assertEquals(REJECTED, competitionParticipant.getStatus());
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
        assertEquals(REJECTED, competitionParticipant.getStatus());
        assertEquals(rejectionReason, competitionParticipant.getRejectionReason());
        assertEquals("", competitionParticipant.getRejectionReasonComment());
    }

    @Test
    public void reject_noComment() throws Exception {
        CompetitionParticipant competitionParticipant = new CompetitionParticipant(invite);
        invite.open();
        competitionParticipant.reject(rejectionReason, empty());
        assertEquals(REJECTED, competitionParticipant.getStatus());
        assertEquals(rejectionReason, competitionParticipant.getRejectionReason());
        assertNull(competitionParticipant.getRejectionReasonComment());
    }
}
