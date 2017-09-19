package org.innovateuk.ifs.assessment.repository;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.ImmutableSet.of;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.invite.constant.InviteStatus.*;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class CompetitionInviteRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionInviteRepository> {

    private final long INNOVATION_AREA_ID = 5L;

    private Competition competition;

    private InnovationArea innovationArea;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    @Override
    protected void setRepository(CompetitionInviteRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        competition = competitionRepository.save(newCompetition().withName("competition").build());
        innovationArea = innovationAreaRepository.save(newInnovationArea().withName("innovation area").build());
    }

    @Test
    public void findAll() {
        repository.save(new CompetitionInvite("name1", "tom@poly.io", "hash", competition, innovationArea));
        repository.save(new CompetitionInvite("name2", "tom@2poly.io", "hash2", competition, innovationArea));

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
    public void getByInviteIds() {
        List<CompetitionInvite> invites = newCompetitionInvite()
                .withName("fred", "jake")
                .withEmail("fred@test.com", "jake@test.com")
                .withHash("hash1", "hash2")
                .withCompetition(competition)
                .withInnovationArea(innovationArea)
                .build(2);

        CompetitionInvite saved1 = repository.save(invites.get(0));
        CompetitionInvite saved2 = repository.save(invites.get(1));

        flushAndClearSession();

        List<CompetitionInvite> retrievedInvites = repository.getByIdIn(asList(saved1.getId(), saved2.getId()));
        assertNotNull(retrievedInvites);

        assertEquals("fred", retrievedInvites.get(0).getName());
        assertEquals("fred@test.com", retrievedInvites.get(0).getEmail());
        assertEquals("hash1", retrievedInvites.get(0).getHash());
        assertEquals(saved1.getId(), retrievedInvites.get(0).getId());

        assertEquals("jake", retrievedInvites.get(1).getName());
        assertEquals("jake@test.com", retrievedInvites.get(1).getEmail());
        assertEquals("hash2", retrievedInvites.get(1).getHash());
        assertEquals(saved2.getId(), retrievedInvites.get(1).getId());
    }

    @Test
    public void getByEmailAndCompetitionId() {
        CompetitionInvite invite = new CompetitionInvite("name", "tom@poly.io", "hash", competition, innovationArea);
        CompetitionInvite saved = repository.save(invite);

        CompetitionInvite retrievedInvite = repository.getByEmailAndCompetitionId("tom@poly.io", competition.getId());
        assertNotNull(retrievedInvite);

        assertEquals(saved, retrievedInvite);
    }

    @Test
    public void getByCompetitionIdAndStatus() {
        Competition otherCompetition = newCompetition().build();

        repository.save(newCompetitionInvite()
                .with(id(null))
                .withCompetition(competition, otherCompetition, competition, otherCompetition, competition, otherCompetition)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com", "michael@example.com", "rachel@example.com")
                .withHash("1dc914e2-d076-4b15-9fa6-99ee5b711613", "bddd15e6-9e9d-42e8-88b0-42f3abcbb26e", "0253e4b9-8f76-4a55-b40b-689a025a9129",
                        "cba968ac-d792-4f41-b3d2-8a92980d54ce", "9e6032a5-39d5-4ec8-a8b2-883f5956a809", "75a615bf-3e5a-4ae3-9479-1753ae438108")
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero", "Michael King", "Rachel Fish")
                .withStatus(CREATED, CREATED, OPENED, OPENED, SENT, SENT)
                .build(6));

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        Page<CompetitionInvite> pageResult = repository.getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);

        List<CompetitionInvite> retrievedInvites = pageResult.getContent();

        assertEquals(1, retrievedInvites.size());

        assertEquals(competition, retrievedInvites.get(0).getTarget());
        assertEquals("john@example.com", retrievedInvites.get(0).getEmail());
        assertEquals("1dc914e2-d076-4b15-9fa6-99ee5b711613", retrievedInvites.get(0).getHash());
        assertEquals("John Barnes", retrievedInvites.get(0).getName());
        assertEquals(CREATED, retrievedInvites.get(0).getStatus());
    }

    @Test
    public void getByCompetitionIdAndStatus_asList() throws Exception {
        Competition otherCompetition = newCompetition().build();

        repository.save(newCompetitionInvite()
                .with(id(null))
                .withCompetition(competition, otherCompetition, competition, otherCompetition, competition, otherCompetition)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com", "michael@example.com", "rachel@example.com")
                .withHash("1dc914e2-d076-4b15-9fa6-99ee5b711613", "bddd15e6-9e9d-42e8-88b0-42f3abcbb26e", "0253e4b9-8f76-4a55-b40b-689a025a9129",
                        "cba968ac-d792-4f41-b3d2-8a92980d54ce", "9e6032a5-39d5-4ec8-a8b2-883f5956a809", "75a615bf-3e5a-4ae3-9479-1753ae438108")
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero", "Michael King", "Rachel Fish")
                .withStatus(CREATED, CREATED, OPENED, OPENED, SENT, SENT)
                .build(6));

        List<CompetitionInvite> retrievedInvites = repository.getByCompetitionIdAndStatus(competition.getId(), CREATED);

        assertEquals(1, retrievedInvites.size());

        assertEquals(competition, retrievedInvites.get(0).getTarget());
        assertEquals("john@example.com", retrievedInvites.get(0).getEmail());
        assertEquals("1dc914e2-d076-4b15-9fa6-99ee5b711613", retrievedInvites.get(0).getHash());
        assertEquals("John Barnes", retrievedInvites.get(0).getName());
        assertEquals(CREATED, retrievedInvites.get(0).getStatus());
    }

    @Test
    public void countByCompetitionIdAndStatus() {
        Competition otherCompetition = newCompetition().build();

        repository.save(newCompetitionInvite()
                .with(id(null))
                .withCompetition(competition, otherCompetition, competition, otherCompetition, competition, otherCompetition)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com", "michael@example.com", "rachel@example.com")
                .withHash("1dc914e2-d076-4b15-9fa6-99ee5b711613", "bddd15e6-9e9d-42e8-88b0-42f3abcbb26e", "0253e4b9-8f76-4a55-b40b-689a025a9129",
                        "cba968ac-d792-4f41-b3d2-8a92980d54ce", "9e6032a5-39d5-4ec8-a8b2-883f5956a809", "75a615bf-3e5a-4ae3-9479-1753ae438108")
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero", "Michael King", "Rachel Fish")
                .withStatus(CREATED, CREATED, OPENED, OPENED, SENT, SENT)
                .build(6));

        long count = repository.countByCompetitionIdAndStatusIn(competition.getId(), of(CREATED, OPENED));
        assertEquals(2, count);
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
        repository.save(new CompetitionInvite("name1", "tom@poly.io", "sameHash", competition, innovationArea));
        repository.save(new CompetitionInvite("name2", "tom@2poly.io", "sameHash", competition, innovationArea));
    }

    @Test
    public void deleteByCompetitionIdAndStatus() throws Exception {
        CompetitionInvite invite1 = new CompetitionInvite("Test Tester 1", "test2@test.com", "hash1", competition, innovationArea);
        CompetitionInvite invite2 = new CompetitionInvite("Test Tester 2", "test1@test.com", "hash2", competition, innovationArea);

        HashSet<InviteStatus> inviteStatuses = newHashSet(CREATED);

        repository.save(asList(invite1, invite2));
        flushAndClearSession();

        assertEquals(2, repository.countByCompetitionIdAndStatusIn(competition.getId(), inviteStatuses));

        repository.deleteByCompetitionIdAndStatus(competition.getId(), CREATED);
        flushAndClearSession();

        assertEquals(0, repository.countByCompetitionIdAndStatusIn(competition.getId(), inviteStatuses));
    }

    @Test
    public void findAssessorsByCompetitionAndInnovationArea() throws Exception {
        long competitionId = 1L;

        addTestAssessors();

        assertEquals(6, userRepository.findByRolesName(ASSESSOR.getName()).size());

        Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "firstName"));

        Page<User> pagedUsers = repository.findAssessorsByCompetitionAndInnovationArea(competitionId, INNOVATION_AREA_ID, pageable);

        assertEquals(4, pagedUsers.getTotalElements());
        assertEquals(1, pagedUsers.getTotalPages());
        assertEquals(4, pagedUsers.getContent().size());
        assertEquals(0, pagedUsers.getNumber());
        assertEquals("Andrew", pagedUsers.getContent().get(0).getFirstName());
        assertEquals("James", pagedUsers.getContent().get(1).getFirstName());
        assertEquals("Jessica", pagedUsers.getContent().get(2).getFirstName());
        assertEquals("Victoria", pagedUsers.getContent().get(3).getFirstName());
    }

    @Test
    public void findAssessorsByCompetition_nextPage() throws Exception {
        long competitionId = 1L;

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();

        addTestAssessors();

        assertEquals(6, userRepository.findByRolesName(ASSESSOR.getName()).size());

        saveInvite(competition, userMapper.mapToDomain(getPaulPlum()));
        saveInvite(competition, userMapper.mapToDomain(getFelixWilson()));

        Pageable pageable = new PageRequest(1, 2, new Sort(Sort.Direction.ASC, "firstName"));

        Page<User> pagedUsers = repository.findAssessorsByCompetition(competitionId, pageable);

        assertEquals(4, pagedUsers.getTotalElements());
        assertEquals(2, pagedUsers.getTotalPages());
        assertEquals(2, pagedUsers.getContent().size());
        assertEquals(1, pagedUsers.getNumber());
        assertEquals("Jessica", pagedUsers.getContent().get(0).getFirstName());
        assertEquals("Victoria", pagedUsers.getContent().get(1).getFirstName());
    }

    @Test
    public void findAssessorsByCompetition() throws Exception {
        long competitionId = 1L;

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();

        addTestAssessors();

        assertEquals(6, userRepository.findByRolesName(ASSESSOR.getName()).size());

        saveInvite(competition, userMapper.mapToDomain(getPaulPlum()));
        saveInvite(competition, userMapper.mapToDomain(getFelixWilson()));

        Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "firstName"));

        Page<User> pagedUsers = repository.findAssessorsByCompetition(competitionId, pageable);

        assertEquals(4, pagedUsers.getTotalElements());
        assertEquals(1, pagedUsers.getTotalPages());
        assertEquals(0, pagedUsers.getNumber());
        assertEquals(4, pagedUsers.getContent().size());
        assertEquals("Andrew", pagedUsers.getContent().get(0).getFirstName());
        assertEquals("James", pagedUsers.getContent().get(1).getFirstName());
        assertEquals("Jessica", pagedUsers.getContent().get(2).getFirstName());
        assertEquals("Victoria", pagedUsers.getContent().get(3).getFirstName());
    }

    private void addTestAssessors() {
        loginSteveSmith();

        InnovationArea innovationArea = innovationAreaRepository.findOne(INNOVATION_AREA_ID);

        List<Profile> profiles = newProfile()
                .withId()
                .withInnovationArea(innovationArea)
                .build(4);

        List<Profile> savedProfiles = Lists.newArrayList(profileRepository.save(profiles));

        Long[] profileIds = simpleMap(savedProfiles, Profile::getId).toArray(new Long[savedProfiles.size()]);

        Role assessorRole = roleRepository.findOneByName(ASSESSOR.getName());

        List<User> users = newUser()
                .withId()
                .withUid("uid1", "uid2", "uid3", "uid4")
                .withFirstName("Victoria", "James", "Jessica", "Andrew")
                .withLastName("Beckham", "Blake", "Alba", "Marr")
                .withRoles(singleton(assessorRole))
                .withProfileId(profileIds[0], profileIds[1], profileIds[2], profileIds[3])
                .build(4);

        userRepository.save(users);
        flushAndClearSession();
    }
    private void saveInvite(Competition competition, User user) {
        CompetitionInvite invite = new CompetitionInvite(user, CompetitionInvite.generateInviteHash(), competition);
        repository.save(invite);
    }
}
