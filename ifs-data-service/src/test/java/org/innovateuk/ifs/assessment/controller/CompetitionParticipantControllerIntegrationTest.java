package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.mapper.CompetitionParticipantRoleMapper;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
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
