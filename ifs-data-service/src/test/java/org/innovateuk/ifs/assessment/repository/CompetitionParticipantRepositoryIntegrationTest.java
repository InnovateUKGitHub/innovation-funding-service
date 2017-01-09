package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.repository.CategoryRepository;
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

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInviteWithoutId;
import static org.innovateuk.ifs.category.builder.CategoryBuilder.newCategory;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.zip;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CompetitionParticipantRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionParticipantRepository> {

    private Competition competition;
    private Category innovationArea;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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
        innovationArea = categoryRepository.save(newCategory().withName("innovation area").withType(INNOVATION_AREA).build());
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
    public void getByUserIdAndRole() {
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

    @Test
    public void getByCompetitionAndRole() {
        List<Competition> competitions = newCompetition().withId(1L, 7L).build(2);

        List<CompetitionParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name2")
                        .withEmail("test1@test.com", "test2@test.com")
                        .withHash(generateInviteHash(), generateInviteHash())
                        .withCompetition(competitions.get(0), competitions.get(1))
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(2)
        );
        flushAndClearSession();

        List<CompetitionParticipant> retrievedParticipants = repository.getByCompetitionIdAndRole(competitions.get(0).getId(), ASSESSOR);

        assertNotNull(retrievedParticipants);
        assertEquals(1, retrievedParticipants.size());
        assertEquals(savedParticipants.get(0), retrievedParticipants.get(0));
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

    private CompetitionParticipant saveNewCompetitionParticipant(CompetitionInvite invite) {
        CompetitionParticipant saved = repository.save(new CompetitionParticipant(invite));
        return saved;
    }

    private List<CompetitionParticipant> saveNewCompetitionParticipants(List<CompetitionInvite> invites) {
        List<CompetitionParticipant> saved = invites.stream().map(competitionInvite ->
                repository.save(new CompetitionParticipant(competitionInvite))).collect(toList());
        return saved;
    }

    private void assertEqualParticipants(List<CompetitionParticipant> expected, List<CompetitionParticipant> actual) {
        zip(expected, actual, (expectedCompetitionParticipant, actualCompetitionParticipant) ->
                assertEqualParticipants(expectedCompetitionParticipant, actualCompetitionParticipant));
    }

    private void assertEqualParticipants(CompetitionParticipant expected, CompetitionParticipant actual) {
        assertNotNull(expected);
        assertNotNull(actual);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getRejectionReasonComment(), actual.getRejectionReasonComment());
        assertEquals(expected.getRole(), actual.getRole());
        assertEquals(expected.getStatus(), actual.getStatus());

        assertTrue((expected.getProcess() == null && actual.getProcess() == null) ||
                (expected.getProcess() != null && actual.getProcess() != null &&
                        expected.getProcess().getId().equals(actual.getProcess().getId()))
        );

        assertTrue((expected.getUser() == null && actual.getUser() == null) ||
                (expected.getUser() != null && actual.getUser() != null &&
                        expected.getUser().getId().equals(actual.getUser().getId())));

        assertTrue((expected.getInvite() == null && actual.getInvite() == null) ||
                (expected.getInvite() != null && actual.getInvite() != null &&
                        expected.getInvite().getId().equals(actual.getInvite().getId())));

        assertTrue((expected.getRejectionReason() == null && actual.getRejectionReason() == null) ||
                (expected.getRejectionReason() != null && actual.getRejectionReason() != null &&
                        expected.getRejectionReason().getId().equals(actual.getRejectionReason().getId())));
    }
}
