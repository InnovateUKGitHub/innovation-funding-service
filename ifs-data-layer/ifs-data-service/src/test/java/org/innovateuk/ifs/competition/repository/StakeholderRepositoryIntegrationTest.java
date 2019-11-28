package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;

@Rollback
public class StakeholderRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<StakeholderRepository> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private Competition competition;

    @Autowired
    @Override
    protected void setRepository(StakeholderRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        loginCompAdmin();

        competition = competitionRepository.save(newCompetition()
                .withId(100L)
                .withName("competition")
                .build());

        setLoggedInUser(null);
    }

    @Test
    public void findByStakeholderId() {

        User expectedUser = newUser().build();
        User otherUser = newUser().build();

        Stakeholder expectedStakeholder = new Stakeholder(competition, expectedUser);
        Stakeholder otherStakeholder = new Stakeholder(competition, otherUser);

        repository.saveAll(asList(expectedStakeholder, otherStakeholder));

        List<Stakeholder> retrievedStakeholders = repository.findByStakeholderId(expectedUser.getId());

        assertFalse(retrievedStakeholders.isEmpty());
        assertEquals(1, retrievedStakeholders.size());
        assertEquals(retrievedStakeholders.get(0).getUser().getId(), expectedUser.getId());
    }

    @Test
    public void findStakeholderByCompetitionIdAndEmail() {

        loginSteveSmith();

        User expectedUser = userRepository.save(new User("New", "User", "new@example.com", "", "my-uid"));

        Stakeholder expectedStakeholder = new Stakeholder(competition, expectedUser);

        repository.save(expectedStakeholder);

        boolean foundExpectedUser = repository.existsByCompetitionIdAndStakeholderEmail(competition.getId(), expectedUser.getEmail());

        assertTrue(foundExpectedUser);
    }

    @Test
    public void findStakeholderByCompetitionIdAndUserId() {
        User expectedUser = newUser().build();
        Stakeholder expectedStakeholder = new Stakeholder(competition, expectedUser);

        repository.save(expectedStakeholder);

        boolean foundExpectedUser = repository.existsByCompetitionIdAndUserId(competition.getId(), expectedUser.getId());
        assertTrue(foundExpectedUser);
    }
}
