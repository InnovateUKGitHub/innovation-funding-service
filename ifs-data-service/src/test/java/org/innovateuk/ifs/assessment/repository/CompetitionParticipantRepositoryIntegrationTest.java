package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInviteWithoutId;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompetitionParticipantRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionParticipantRepository> {

    private Competition competition;
    private InnovationArea innovationArea;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;

    @Autowired
    @Override
    protected void setRepository(CompetitionParticipantRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        competition = competitionRepository.save(newCompetition().withName("competition").build());
        innovationArea = innovationAreaRepository.save(newInnovationArea().withName("innovation area").build());
    }

    @Test
    public void findAll() {
        CompetitionInvite invite1 = buildNewCompetitionInvite("name1", "test1@test.com", "hash", SENT);
        CompetitionInvite invite2 = buildNewCompetitionInvite("name1", "test2@test.com", "hash2", SENT);

        repository.save(new CompetitionParticipant(invite1));
        repository.save(new CompetitionParticipant(invite2));

        Iterable<CompetitionParticipant> invites = repository.findAll();

        assertEquals(2, invites.spliterator().getExactSizeIfKnown());
    }

    @Test
    public void getByInviteHash() {
        CompetitionInvite invite = buildNewCompetitionInvite("name1", "test1@test.com", "hash", SENT);
        CompetitionParticipant savedParticipant = repository.save(new CompetitionParticipant(invite));

        flushAndClearSession();

        CompetitionParticipant retrievedParticipant = repository.getByInviteHash("hash");
        assertNotNull(retrievedParticipant);
        assertEquals(savedParticipant, retrievedParticipant);

        assertEquals(ASSESSOR, retrievedParticipant.getRole());
        assertEquals(ParticipantStatus.PENDING, retrievedParticipant.getStatus());

        CompetitionInvite retrievedInvite = retrievedParticipant.getInvite();
        assertEquals("name1", retrievedInvite.getName());
        assertEquals("test1@test.com", retrievedInvite.getEmail());
        assertEquals("hash", retrievedInvite.getHash());
        assertEquals(savedParticipant.getInvite().getId(), retrievedInvite.getId());

        Competition retrievedCompetition = retrievedParticipant.getProcess();
        assertEquals(competition.getName(), retrievedCompetition.getName());

        assertEquals(retrievedInvite.getTarget().getId(), retrievedCompetition.getId());
    }

    @Test
    public void save() {
        CompetitionInvite invite = buildNewCompetitionInvite("name1", "test1@test.com", "hash", SENT);
        CompetitionParticipant savedParticipant = repository.save(new CompetitionParticipant(invite));

        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id);

        assertNotNull(retrievedParticipant);
        assertEquals(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_accepted() {
        User user = newUser().build();

        CompetitionInvite invite = buildNewCompetitionInvite("name1", "test1@test.com", "hash", OPENED);

        CompetitionParticipant savedParticipant = repository.save(
                (new CompetitionParticipant(invite)).acceptAndAssignUser(user));

        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id); // not setting the state

        assertNotNull(retrievedParticipant);
        assertEquals(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_rejected() {
        CompetitionInvite invite = buildNewCompetitionInvite("name1", "test1@test.com", "hash", OPENED);

        RejectionReason reason = rejectionReasonRepository.findAll().get(0);
        CompetitionParticipant savedParticipant = repository.save((new CompetitionParticipant(invite)).reject(reason, Optional.of("too busy")));

        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id);

        assertNotNull(retrievedParticipant);
        assertEquals(savedParticipant, retrievedParticipant);
    }

    @Test
    public void getByUserRoleStatus() {
        User user = newUser()
                .withFirstName("Professor")
                .build();

        CompetitionInvite invite = buildNewCompetitionInvite("name1", "test1@test.com", "hash", OPENED);
        invite.setUser(user);

        CompetitionParticipant savedParticipant = repository.save(new CompetitionParticipant(invite));
        flushAndClearSession();

        List<CompetitionParticipant> retrievedParticipants = repository.getByUserIdAndRole(user.getId(), ASSESSOR);

        assertNotNull(retrievedParticipants);
        assertEquals(1, retrievedParticipants.size());
        assertEquals(savedParticipant, retrievedParticipants.get(0));

        assertEquals(ASSESSOR, retrievedParticipants.get(0).getRole());
        assertEquals(ParticipantStatus.PENDING, retrievedParticipants.get(0).getStatus());

        Competition retrievedCompetition = retrievedParticipants.get(0).getProcess();
        assertEquals(competition.getName(), retrievedCompetition.getName());

        User retrievedUser = retrievedParticipants.get(0).getUser();
        assertEquals(user.getFirstName(), retrievedUser.getFirstName());
    }

    private CompetitionInvite buildNewCompetitionInvite(String name, String email, String hash, InviteStatus status) {
        return newCompetitionInviteWithoutId() // added this to prevent so we can persist
                .withName(name)
                .withEmail(email)
                .withHash(hash)
                .withCompetition(competition)
                .withInnovationArea(innovationArea)
                .withStatus(status)
                .build();
    }
}
