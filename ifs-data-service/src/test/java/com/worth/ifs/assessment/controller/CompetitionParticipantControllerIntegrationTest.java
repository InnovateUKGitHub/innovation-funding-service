package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.invite.mapper.CompetitionParticipantRoleMapper;
import com.worth.ifs.invite.mapper.ParticipantStatusMapper;
import com.worth.ifs.invite.repository.CompetitionInviteRepository;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static com.worth.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static com.worth.ifs.invite.constant.InviteStatus.CREATED;
import static com.worth.ifs.invite.constant.InviteStatus.OPENED;
import static com.worth.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static com.worth.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static com.worth.ifs.invite.domain.ParticipantStatus.PENDING;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.junit.Assert.assertTrue;


public class CompetitionParticipantControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionParticipantController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(CompetitionParticipantController controller) {
        this.controller = controller;
    }

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CompetitionInviteRepository competitionInviteRepository;

    @Before
    public void setUp() throws Exception {
        loginPaulPlum();

        Competition competition = competitionRepository.findOne(1L);
        competitionParticipantRepository.save( newCompetitionParticipant()
                .with(id(null))
                .withCompetition(competition)
                .withUser(newUser()
                        .withId(3L)
                        .withFirstName("Professor")
                )
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("name")
                        .withEmail("tom@poly.io")
                        .withHash("hash")
                        .withCompetition(competition)
                        .withStatus(CREATED)
                )
                .withStatus(PENDING)
                .withRole(ASSESSOR)
                .build()
        );

        competitionParticipantRepository.save( newCompetitionParticipant()
                .with(id(null))
                .withCompetition(competition)
                .withUser(newUser()
                        .withId(3L)
                        .withFirstName("Professor")
                )
                .withInvite(newCompetitionInvite()
                        .with(id(null))
                        .withName("fred")
                        .withEmail("fred@test.com")
                        .withHash("hashkey")
                        .withCompetition(competition)
                        .withStatus(OPENED)
                )
                .withStatus(ACCEPTED)
                .withRole(ASSESSOR)
                .build()
        );

        flushAndClearSession();
    }

    @Test
    public void getParticipants() {
        List<CompetitionParticipantResource> participants = controller.getParticipants(
                getPaulPlum().getId(),
                CompetitionParticipantRoleResource.ASSESSOR,
                ParticipantStatusResource.PENDING)
                .getSuccessObject();

        assertEquals(1, participants.size());
        assertEquals(Long.valueOf(1L), participants.get(0).getCompetitionId());
        assertEquals(Long.valueOf(3L), participants.get(0).getUserId());
    }

    @Test
    public void getParticipants_differentUser() {
        loginFelixWilson();

        List<CompetitionParticipantResource> participants = controller.getParticipants(
                getPaulPlum().getId(),
                CompetitionParticipantRoleResource.ASSESSOR,
                ParticipantStatusResource.PENDING)
                .getSuccessObject();

        assertTrue(participants.isEmpty());
    }

    @Test
    public void getParticipants_accepted() {
        List<CompetitionParticipantResource> participants = controller.getParticipants(
                getPaulPlum().getId(),
                CompetitionParticipantRoleResource.ASSESSOR,
                ParticipantStatusResource.ACCEPTED)
                .getSuccessObject();

        assertEquals(1, participants.size());
        assertEquals(Long.valueOf(1L), participants.get(0).getCompetitionId());
        assertEquals(Long.valueOf(3L), participants.get(0).getUserId());
        assertEquals(1L, participants.get(0).getSubmittedAssessments());
        assertEquals(3L, participants.get(0).getTotalAssessments());
    }
}
