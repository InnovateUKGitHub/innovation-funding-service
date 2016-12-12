package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.repository.CategoryRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.category.builder.CategoryBuilder.newCategory;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        competition = competitionRepository.save( newCompetition().withName("competition").build()) ;
        innovationArea = categoryRepository.save( newCategory().withName("innovation area").withType(INNOVATION_AREA).build() );
    }

    @Test
    public void findAll() {
        CompetitionInvite invite1 = new CompetitionInvite("name1", "tom1@poly.io", "hash", competition, innovationArea);
        CompetitionInvite invite2 = new CompetitionInvite("name2", "tom2@poly.io", "hash2", competition, innovationArea);

        repository.save( new CompetitionParticipant(invite1) );
        repository.save( new CompetitionParticipant(invite2) );

        Iterable<CompetitionParticipant> invites = repository.findAll();

        assertEquals(2, invites.spliterator().getExactSizeIfKnown());
    }

    @Test
    public void getByInviteHash() {
        CompetitionInvite invite = new CompetitionInvite("name1", "tom1@poly.io", "hash", competition, innovationArea);
        CompetitionParticipant savedParticipant = repository.save( new CompetitionParticipant(invite) );

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
        CompetitionInvite invite = new CompetitionInvite("name1", "tom1@poly.io", "hash", competition, innovationArea);
        CompetitionParticipant savedParticipant = repository.save( new CompetitionParticipant(invite) );

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
                (new CompetitionParticipant(invite)).acceptAndAssignUser(user) );

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
        CompetitionParticipant savedParticipant = repository.save( (new CompetitionParticipant(invite)).reject(reason, Optional.of("too busy")) );

        flushAndClearSession();

        long id = savedParticipant.getId();

        CompetitionParticipant retrievedParticipant = repository.findOne(id);

        assertNotNull(retrievedParticipant);
        assertEquals(savedParticipant, retrievedParticipant);
    }

    @Test
    public void getByUserRoleStatus() {
        CompetitionInvite invite = new CompetitionInvite("name1", "tom1@poly.io", "hash", competition, innovationArea);
        User user = newUser()
                .withId(3L)
                .withFirstName("Professor")
                .build();
        CompetitionParticipant savedParticipant = repository.save( new CompetitionParticipant(user, invite));
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
