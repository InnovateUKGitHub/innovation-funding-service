package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.repository.CompetitionInviteRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static com.worth.ifs.category.builder.CategoryBuilder.newCategory;
import static com.worth.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompetitionInviteRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionInviteRepository> {

    private Competition competition;

    private Category innovationArea;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    @Override
    protected void setRepository(CompetitionInviteRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        competition = competitionRepository.save( newCompetition().withName("competition").build() );
        innovationArea = categoryRepository.save( newCategory().withName("innovation area").withType(INNOVATION_AREA).build() );
    }

    @Test
    public void findAll() {
        repository.save( new CompetitionInvite("name1", "tom@poly.io", "hash", competition, innovationArea)  );
        repository.save( new CompetitionInvite("name2", "tom@2poly.io", "hash2", competition, innovationArea)  );

        Iterable<CompetitionInvite> invites = repository.findAll();

        assertEquals(2, invites.spliterator().getExactSizeIfKnown());
    }

    @Test
    public void getByHash() {
        CompetitionInvite invite = new CompetitionInvite("name", "tom@poly.io", "hash", competition, innovationArea);
        CompetitionInvite saved = repository.save(invite);

        flushAndClearSession();

        CompetitionInvite retrievedInvite = repository.getByHash("hash");
        assertNotNull(retrievedInvite);

        assertEquals("name", retrievedInvite.getName());
        assertEquals("tom@poly.io", retrievedInvite.getEmail());
        assertEquals("hash", retrievedInvite.getHash());
        assertEquals(saved.getTarget().getId(), retrievedInvite.getTarget().getId());
    }

    @Test
    public void getByEmailAndTargetId() {
        CompetitionInvite invite = new CompetitionInvite("name", "tom@poly.io", "hash", competition, innovationArea);
        CompetitionInvite saved = repository.save(invite);

        CompetitionInvite retrievedInvite = repository.getByEmailAndCompetitionId("tom@poly.io", competition.getId());
        assertNotNull(retrievedInvite);

        assertEquals(saved, retrievedInvite);
    }

    @Test
    public void save() {
        CompetitionInvite invite = new CompetitionInvite("name", "tom@poly.io", "hash", competition, innovationArea);
        repository.save(invite);

        flushAndClearSession();

        long id = invite.getId();

        CompetitionInvite retrievedInvite = repository.findOne(id);

        assertEquals("name", retrievedInvite.getName());
        assertEquals("tom@poly.io", retrievedInvite.getEmail());
        assertEquals("hash", retrievedInvite.getHash());
        assertEquals(competition.getId(), retrievedInvite.getTarget().getId());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void save_duplicateHash() {
        repository.save( new CompetitionInvite("name1", "tom@poly.io", "sameHash", competition, innovationArea)  );
        repository.save( new CompetitionInvite("name2", "tom@2poly.io", "sameHash", competition, innovationArea)  );
    }
}
