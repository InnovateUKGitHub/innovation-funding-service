package com.worth.ifs.assessment.domain;

import com.worth.ifs.competition.builder.CompetitionBuilder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.domain.CompetitionInvite;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.invite.constant.InviteStatus.OPENED;
import static com.worth.ifs.invite.constant.InviteStatus.CREATED;
import static com.worth.ifs.invite.constant.InviteStatus.SENT;
import static org.junit.Assert.assertEquals;

public class CompetitionInviteTest {

    private CompetitionInvite invite;
    private Competition competition;

    @Before
    public void setup() {
        competition = CompetitionBuilder.newCompetition().build();
        invite = new CompetitionInvite("invite name", "email", "hash", competition);
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
        invite.send();
        assertEquals(SENT, invite.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void send_sent() {
        invite.send().send();
        assertEquals(SENT, invite.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void send_opened() {
        invite.open().send();
    }

    @Test
    public void open_created() {
        // this probably shouldn't allow an invite that hasn't been sent from being opened
        invite.open();
        assertEquals(OPENED, invite.getStatus());
    }

    @Test
    public void open_sent() {
        invite.send().open();
        assertEquals(OPENED, invite.getStatus());
    }

    @Test
    public void open_opened() {
        invite.open().open();
        assertEquals(OPENED, invite.getStatus());
    }
}
