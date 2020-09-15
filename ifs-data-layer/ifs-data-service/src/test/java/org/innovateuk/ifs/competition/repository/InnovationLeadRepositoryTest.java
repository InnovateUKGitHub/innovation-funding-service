package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InnovationLeadRepositoryTest extends BaseRepositoryIntegrationTest<InnovationLeadRepository> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InnovationLeadRepository innovationLeadRepository;

    @Autowired
    @Override
    protected void setRepository(InnovationLeadRepository repository) {
        this.repository = repository;
    }

    @Rollback
    @Test
    public void findAvailableInnovationLeadsNotAssignedToCompetition() {

        loginCompAdmin();

        User innovationLeadOnCompetition = userRepository.save(newUser()
                .with(id(null))
                .withCreatedBy(newUser().with(id(null)).build())
                .withUid("uid-5")
                .build());

        Competition competition = competitionRepository.save(newCompetition()
                .with(id(null))
                .withLeadTechnologist(innovationLeadOnCompetition)
                .withCreatedBy(newUser().build())
                .withCreatedOn(ZonedDateTime.now())
                .build());

        Competition competition2 = competitionRepository.save(newCompetition()
                .with(id(null))
                .withLeadTechnologist(innovationLeadOnCompetition)
                .withCreatedBy(newUser().build())
                .withCreatedOn(ZonedDateTime.now())
                .build());

        User creator = userRepository.save(newUser()
                .with(id(null))
                .withCreatedBy(newUser().with(id(null)).build())
                .withUid("uid-1")
                .build());

        User user = userRepository.save(newUser()
                .with(id(null))
                .withUid("uid-2")
                .withRoles(singleton(INNOVATION_LEAD))
                .withStatus(ACTIVE)
                .withCreatedBy(creator)
                .withCreatedOn(ZonedDateTime.now())
                .build());

        User user2 = userRepository.save(newUser()
                .with(id(null))
                .withUid("uid-3")
                .withRoles(singleton(INNOVATION_LEAD))
                .withStatus(ACTIVE)
                .withCreatedBy(creator)
                .withCreatedOn(ZonedDateTime.now())
                .build());

        InnovationLead innovationLead = new InnovationLead(competition2, user);
        InnovationLead innovationLead2 = new InnovationLead(competition2, user2);
        innovationLeadRepository.saveAll(asList(innovationLead, innovationLead2));

        flushAndClearSession();

        List<User> innovationLeads = repository.findAvailableInnovationLeadsNotAssignedToCompetition(competition.getId());

        assertTrue(innovationLeads.stream().anyMatch(u -> u.getId().equals(user.getId())));
        assertTrue(innovationLeads.stream().anyMatch(u -> u.getId().equals(user2.getId())));
    }

    @Rollback
    @Test
    public void findInnovationsLeadsAssignedToCompetition() {

        loginCompAdmin();

        User innovationLeadonCompetition = userRepository.save(newUser()
                .with(id(null))
                .withCreatedBy(newUser().with(id(null)).build())
                .withUid("uid-5")
                .build());

        Competition competition = competitionRepository.save(newCompetition()
                .with(id(null))
                .withLeadTechnologist(innovationLeadonCompetition)
                .withCreatedBy(newUser().build())
                .withCreatedOn(ZonedDateTime.now())
                .build());

        User creator = userRepository.save(newUser()
                .with(id(null))
                .withCreatedBy(newUser().with(id(null)).build())
                .withUid("uid-1")
                .build());

        User user = userRepository.save(newUser()
                .with(id(null))
                .withUid("uid-2")
                .withRoles(singleton(INNOVATION_LEAD))
                .withStatus(ACTIVE)
                .withCreatedBy(creator)
                .withCreatedOn(ZonedDateTime.now())
                .build());

        User user2 = userRepository.save(newUser()
                .with(id(null))
                .withUid("uid-3")
                .withRoles(singleton(INNOVATION_LEAD))
                .withStatus(ACTIVE)
                .withCreatedBy(creator)
                .withCreatedOn(ZonedDateTime.now())
                .build());

        InnovationLead innovationLead = new InnovationLead(competition, user);
        InnovationLead innovationLead2 = new InnovationLead(competition, user2);
        innovationLeadRepository.saveAll(asList(innovationLead, innovationLead2));

        flushAndClearSession();

        List<User> innovationLeads = repository.findInnovationsLeadsAssignedToCompetition(competition.getId());

        assertEquals(2, innovationLeads.size());
    }
}