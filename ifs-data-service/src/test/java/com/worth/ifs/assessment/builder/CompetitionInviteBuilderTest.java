package com.worth.ifs.assessment.builder;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.invite.constant.InviteStatus.OPENED;
import static com.worth.ifs.invite.constant.InviteStatus.SENT;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;

public class CompetitionInviteBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 7L;
        InviteStatus expectedStatus = SENT;
        String expectedEmail = "tom@poly.io";
        User expectedUser = newUser().withId(5L).build();
        Competition expectedCompetition = newCompetition().withName("Juggling Craziness").build();
        String expectedHash = "68656c6c6f";
        String expectedName = "paul plum";


        CompetitionInvite invite = newCompetitionInvite()
                .withId(expectedId)
                .withStatus(expectedStatus)
                .withEmail(expectedEmail)
                .withUser(expectedUser)
                .withCompetition(expectedCompetition)
                .withHash(expectedHash)
                .withName(expectedName)
                .build();

        assertEquals(expectedId, invite.getId());
        assertEquals(expectedStatus, invite.getStatus());
        assertEquals(expectedEmail, invite.getEmail());
        assertEquals(expectedUser, invite.getUser());
        assertEquals(expectedCompetition, invite.getTarget());
        assertEquals(expectedHash, invite.getHash());
        assertEquals(expectedName, invite.getName());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 7L, 13L };
        InviteStatus[] expectedStatuses = { SENT, OPENED };
        String[] expectedEmails = { "tom@poly.io", "steve.smith@empire.com" };
        User[] expectedUsers = newUser().withId(5L, 11L).buildArray(2, User.class);
        Competition[] expectedCompetitions = newCompetition().withName("Juggling Craziness", "Intermediate Juggling").buildArray(2, Competition.class);
        String[] expectedHashes = { "68656c6c6f", "776f726c64" };
        String[] expectedNames = { "paul plum", "steve smith" };

        List<CompetitionInvite> invites = newCompetitionInvite()
                .withId(expectedIds)
                .withStatus(expectedStatuses)
                .withEmail(expectedEmails)
                .withUser(expectedUsers)
                .withCompetition(expectedCompetitions)
                .withHash(expectedHashes)
                .withName(expectedNames)
                .build(2);

        CompetitionInvite first = invites.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStatuses[0], first.getStatus());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedCompetitions[0], first.getTarget());
        assertEquals(expectedHashes[0], first.getHash());
        assertEquals(expectedNames[0], first.getName());

        CompetitionInvite second = invites.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStatuses[1], second.getStatus());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedCompetitions[1], second.getTarget());
        assertEquals(expectedHashes[1], second.getHash());
        assertEquals(expectedNames[1], second.getName());
    }
}