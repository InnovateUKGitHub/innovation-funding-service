package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.assessment.builder.CompetitionInviteBuilder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.invite.domain.*;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.invite.repository.RejectionReasonRepository;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.invite.constant.InviteStatus.OPENED;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompetitionParticipantRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionParticipantRepository> {

    private Competition competition;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;

    @Autowired
    @Override
    protected void setRepository(CompetitionParticipantRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        competition = competitionRepository.save( newCompetition().withName("competition").build()) ;
    }

    @Test
    public void findAll() {
        CompetitionInvite invite1 = new CompetitionInvite("name1", "tom1@poly.io", "hash", competition);
        CompetitionInvite invite2 = new CompetitionInvite("name2", "tom2@poly.io", "hash2", competition);

        repository.save( new CompetitionParticipant(competition, invite1) );
        repository.save( new CompetitionParticipant(competition, invite2) );

        Iterable<CompetitionParticipant> invites = repository.findAll();

        assertEquals(2, invites.spliterator().getExactSizeIfKnown());
    }

    @Test
    public void getByInviteHash() {
        CompetitionInvite invite = new CompetitionInvite("name1", "tom1@poly.io", "hash", competition);
        CompetitionParticipant savedParticipant = repository.save( new CompetitionParticipant(competition, invite) );

        flushAndClearSession();

        CompetitionParticipant retrievedParticipant = repository.getByInviteHash("hash");
        assertNotNull(retrievedParticipant);
        assertEquals(savedParticipant, retrievedParticipant);

        assertEquals(CompetitionParticipantRole.ASSESSOR, retrievedParticipant.getRole());
        assertEquals(ParticipantStatus.PENDING, retrievedParticipant.getStatus());

        CompetitionInvite retrievedInvite = retrievedParticipant.getInvite();
        assertEquals("name1", retrievedInvite.getName());
        assertEquals("tom1@poly.io", retrievedInvite.getEmail());
        assertEquals("hash", retrievedInvite.getHash());
        assertEquals(savedParticipant.getInvite().getId(), retrievedInvite.getId());

        Competition retrievedCompetition = retrievedParticipant.getProcess();
        assertEquals(competition.getName(), retrievedCompetition.getName());

        assertEquals(retrievedInvite.getTarget().getId(), retrievedCompetition.getId());
    }

    @Test
    public void save() {
        CompetitionInvite invite = new CompetitionInvite("name1", "tom1@poly.io", "hash", competition);
        CompetitionParticipant savedParticipant = repository.save( new CompetitionParticipant(competition, invite) );

        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id);

        assertNotNull(retrievedParticipant);
        assertEquals(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_accepted() {
        User user = newUser().build();

        CompetitionInvite invite = CompetitionInviteBuilder
                .newCompetitionInviteWithoutId() // can we do this for all
                .withName("name1")
                .withEmail("tom@poly.io")
                .withHash("hash")
                .withCompetition(competition)
                .withStatus(OPENED).build();

        CompetitionParticipant savedParticipant = repository.save(
                (new CompetitionParticipant(competition, invite)).acceptAndAssignUser(user) );

        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id); // not setting the state

        assertNotNull(retrievedParticipant);
        assertEquals(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_rejected() {
        CompetitionInvite invite = CompetitionInviteBuilder
                .newCompetitionInviteWithoutId() // added this to prevent so we can persist
                .withName("name1")
                .withEmail("tom@poly.io")
                .withHash("hash")
                .withCompetition(competition)
                .withStatus(OPENED).build();

        RejectionReason reason = rejectionReasonRepository.findAll().get(0);
        CompetitionParticipant savedParticipant = repository.save( (new CompetitionParticipant(competition, invite)).reject(reason, Optional.of("too busy")) );

        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id);

        assertNotNull(retrievedParticipant);
        assertEquals(savedParticipant, retrievedParticipant);
    }

    @Test
    public void getByUserRoleStatus() {
        CompetitionInvite invite = new CompetitionInvite("name1", "tom1@poly.io", "hash", competition);
        User user = newUser()
                .withId(3L)
                .withFirstName("Professor")
                .build();
        CompetitionParticipant savedParticipant = repository.save( new CompetitionParticipant(competition, user, invite));
        flushAndClearSession();

        List<CompetitionParticipant> retrievedParticipants = repository.getByUserIdAndRoleAndStatus(user.getId(),CompetitionParticipantRole.ASSESSOR, ParticipantStatus.PENDING );

        assertNotNull(retrievedParticipants);
        assertEquals(1,retrievedParticipants.size());
        assertEquals(savedParticipant, retrievedParticipants.get(0));

        assertEquals(CompetitionParticipantRole.ASSESSOR, retrievedParticipants.get(0).getRole());
        assertEquals(ParticipantStatus.PENDING, retrievedParticipants.get(0).getStatus());

        Competition retrievedCompetition = retrievedParticipants.get(0).getProcess();
        assertEquals(competition.getName(), retrievedCompetition.getName());

        User retrievedUser = retrievedParticipants.get(0).getUser();
        assertEquals(user.getFirstName(),retrievedUser.getFirstName());
    }
}
