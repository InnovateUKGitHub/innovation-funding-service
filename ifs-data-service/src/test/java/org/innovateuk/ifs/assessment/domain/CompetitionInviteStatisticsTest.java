package org.innovateuk.ifs.assessment.domain;


import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionInviteStatistics;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteStatisticsBuilder.newCompetitionInviteStatistics;
import static org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;
import static org.junit.Assert.assertEquals;

public class CompetitionInviteStatisticsTest {

    private CompetitionInviteStatistics competitionInviteStatistics;
    
    @Before
    public void setup() throws Exception {

        List<CompetitionInvite> competitionInvites = newCompetitionInvite()
                .withStatus(CREATED, OPENED,CREATED, SENT, CREATED, CREATED)
                .build(6);

        List<CompetitionParticipant> competitionParticipants = newCompetitionParticipant()
                .withStatus(ACCEPTED, ACCEPTED, REJECTED, PENDING, ACCEPTED)
                .build(5);

        competitionInviteStatistics = newCompetitionInviteStatistics()
                .withCompetitionParticipants(competitionParticipants)
                .withCompetitionInvites(competitionInvites)
                .build();
    }

    @Test
    public void getInvited() {
        assertEquals(2L, competitionInviteStatistics.getInvited());
    }

    @Test
    public void getAccepted() {
        assertEquals(3L, competitionInviteStatistics.getAccepted());
    }

    @Test
    public void getDeclined() {
        assertEquals(1L ,competitionInviteStatistics.getDeclined());
    }

    @Test
    public void getInviteList() {
        assertEquals(4L, competitionInviteStatistics.getInviteList());
    }
}
