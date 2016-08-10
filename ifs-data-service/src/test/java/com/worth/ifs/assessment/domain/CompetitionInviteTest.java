package com.worth.ifs.assessment.domain;

import com.worth.ifs.competition.builder.CompetitionBuilder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.domain.CompetitionInvite;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.invite.constant.InviteStatusConstants.ACCEPTED;
import static com.worth.ifs.invite.constant.InviteStatusConstants.CREATED;
import static com.worth.ifs.invite.constant.InviteStatusConstants.SEND;
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
        assertEquals(SEND, invite.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void send_sent() {
        invite.send().send();
        assertEquals(SEND, invite.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void send_opened() {
        invite.open().send();
    }

    @Test
    public void open_created() {
        // this probably shouldn't allow an invite that hasn't been sent from being opened
        invite.open();
        assertEquals(ACCEPTED, invite.getStatus());
    }

    @Test
    public void open_sent() {
        invite.send().open();
        assertEquals(ACCEPTED, invite.getStatus());
    }

    @Test
    public void open_opened() {
        invite.open().open();
        assertEquals(ACCEPTED, invite.getStatus());
    }
}
