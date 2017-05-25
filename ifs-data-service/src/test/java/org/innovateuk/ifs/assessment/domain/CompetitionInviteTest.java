package org.innovateuk.ifs.assessment.domain;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

public class CompetitionInviteTest {

    private CompetitionInvite invite;
    private Competition competition;
    private InnovationArea innovationArea;

    @Before
    public void setup() {
        competition = newCompetition().build();
        innovationArea = newInnovationArea().build();
        invite = new CompetitionInvite("invite name", "email", "hash", competition, innovationArea);
    }

    @Test
    public void newInvite() {
        assertEquals(CREATED, invite.getStatus());
        assertEquals("invite name", invite.getName());
        assertEquals("email", invite.getEmail());
        assertEquals("hash", invite.getHash());
        assertEquals(competition, invite.getTarget());
    }

    @Test
    public void send() {
        invite.send(newUser().build(), ZonedDateTime.now());
        assertEquals(SENT, invite.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void send_sent() {
        invite
                .send(newUser().build(), ZonedDateTime.now())
                .send(newUser().build(), ZonedDateTime.now());
        assertEquals(SENT, invite.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void send_opened() {
        invite.open().send(newUser().build(), ZonedDateTime.now());
    }

    @Test
    public void resend() {
        invite
                .send(newUser().build(), ZonedDateTime.now())
                .sendOrResend(newUser().build(), ZonedDateTime.now());
        assertEquals(SENT, invite.getStatus());
    }

    @Test
    public void resend_created() {
        invite.sendOrResend(newUser().build(), ZonedDateTime.now());
        assertEquals(SENT, invite.getStatus());
    }

    @Test
    public void resend_opened() {
        invite
                .open()
                .sendOrResend(newUser().build(), ZonedDateTime.now());
        assertEquals(SENT, invite.getStatus());
    }

    @Test(expected = NullPointerException.class)
    public void sent_nullBy() {
        invite.send(null, ZonedDateTime.now());
    }

    @Test(expected = NullPointerException.class)
    public void sent_nullOn() {
        invite.send(newUser().build(), null);
    }

    @Test
    public void open() {
        invite.send(newUser().build(), ZonedDateTime.now()).open();
        assertEquals(OPENED, invite.getStatus());
    }

    @Test
    public void open_created() {
        invite.open();
        assertEquals(OPENED, invite.getStatus());
    }

    @Test
    public void open_opened() {
        invite.open().open();
        assertEquals(OPENED, invite.getStatus());
    }
}
