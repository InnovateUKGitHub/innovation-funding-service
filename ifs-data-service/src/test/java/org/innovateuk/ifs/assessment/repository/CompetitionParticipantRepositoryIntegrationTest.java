package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.repository.CategoryRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInviteWithoutId;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.CategoryBuilder.newCategory;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElement;
import static org.innovateuk.ifs.util.CollectionFunctions.zip;
import static org.junit.Assert.*;

public class CompetitionParticipantRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionParticipantRepository> {

    private Competition competition;
    private Category innovationArea;
    private User user;

    @Autowired
    private UserRepository userRepository;

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
        user = userRepository.findByEmail("paul.plum@gmail.com")
                .orElseThrow(() -> new IllegalStateException("Expected to find test user for email paul.plum@gmail.com"));
    }

    @Test
    public void findAll() {
        List<CompetitionParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name2")
                        .withEmail("test1@test.com", "test2@test.com")
                        .withHash(generateInviteHash(), generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(2)
        );
        flushAndClearSession();

        List<CompetitionParticipant> retrievedParticipant = repository.findAll();

        assertEquals(2, retrievedParticipant.size());
        assertEqualParticipants(savedParticipants, retrievedParticipant);
    }

    @Test
    public void getByInviteHash() {
        String hash = generateInviteHash();

        CompetitionParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail("test1@test.com")
                        .withHash(hash)
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build()
        );
        flushAndClearSession();

        CompetitionParticipant retrievedParticipant = repository.getByInviteHash(hash);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save() {
        CompetitionParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail("test1@test.com")
                        .withHash(generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build()
        );
        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_accepted() {
        CompetitionParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail(user.getEmail())
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(OPENED)
                        .withUser(user)
                        .build()
        );
        savedParticipant.acceptAndAssignUser(user);
        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void save_rejected() {
        CompetitionParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail("test1@test.com")
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(OPENED)
                        .build()
        );

        RejectionReason reason = rejectionReasonRepository.findAll().get(0);
        savedParticipant.reject(reason, Optional.of("too busy"));
        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id);
        assertEqualParticipants(savedParticipant, retrievedParticipant);
    }

    @Test
    public void getByUserRoleStatus() {
        CompetitionParticipant savedParticipant = saveNewCompetitionParticipant(
                newCompetitionInviteWithoutId()
                        .withName("name1")
                        .withEmail(user.getEmail())
                        .withHash(Invite.generateInviteHash())
                        .withCompetition(competition)
                        .withInnovationArea(innovationArea)
                        .withStatus(OPENED)
                        .withUser(user)
                        .build()
        );
        flushAndClearSession();

        List<CompetitionParticipant> retrievedParticipants = repository.getByUserIdAndRole(user.getId(), ASSESSOR);
        assertEqualParticipants(savedParticipant, getOnlyElement(retrievedParticipants));
    }

    @Test
    public void getByInviteEmail() {
        List<Competition> competitions = newCompetition()
                .with(id(null))
                .build(2);
        competitions.forEach(competition -> competitionRepository.save(competition));

        List<CompetitionParticipant> savedParticipants = saveNewCompetitionParticipants(
                newCompetitionInviteWithoutId()
                        .withName("name1", "name1", "name2")
                        .withEmail("test1@test.com", "test1@test.com", "test2@test.com")
                        .withHash(generateInviteHash(), generateInviteHash(), generateInviteHash())
                        .withCompetition(competitions.get(0), competitions.get(1), competitions.get(0))
                        .withInnovationArea(innovationArea)
                        .withStatus(SENT)
                        .build(3)
        );
        flushAndClearSession();

        List<CompetitionParticipant> retrievedParticipants = repository.getByInviteEmail("test1@test.com");
        assertEqualParticipants(asList(savedParticipants.get(0), savedParticipants.get(1)), retrievedParticipants);
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
