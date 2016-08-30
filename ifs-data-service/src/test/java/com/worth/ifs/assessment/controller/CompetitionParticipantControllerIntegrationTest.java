package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.mapper.CompetitionParticipantRoleMapper;
import com.worth.ifs.invite.mapper.ParticipantStatusMapper;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.security.SecuritySetter.swapOutForUser;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

public class CompetitionParticipantControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionParticipantController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(CompetitionParticipantController controller) {
        this.controller = controller;
    }

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    private CompetitionParticipantRoleMapper competitionParticipantRoleMapper;

    @Autowired
    private ParticipantStatusMapper participantStatusMapper;


    @Before
    public void setUp() throws Exception {
        swapOutForUser(getPaulPlum());

        Competition competition = competitionRepository.findOne(1L);
        User user = newUser()
                .withid(3L)
                .withFirstName("Professor")
                .build();
        competitionParticipantRepository.save( new CompetitionParticipant(competition, user) );
        flushAndClearSession();
    }

    @Test
    public void findByUserIdRoleAndStatus() {
        Long userId = 3L;

        List<CompetitionParticipantResource> participants = controller.getParticipants(userId, CompetitionParticipantRoleResource.ASSESSOR ,ParticipantStatusResource.PENDING).getSuccessObject();
        assertEquals(1, participants.size());
        assertEquals(Long.valueOf(1L), participants.get(0).getCompetitionId());
        assertEquals(Long.valueOf(3L), participants.get(0).getUserId());
    }
}
