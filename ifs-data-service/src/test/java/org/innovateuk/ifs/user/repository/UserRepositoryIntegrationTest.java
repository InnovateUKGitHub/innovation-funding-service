package org.innovateuk.ifs.user.repository;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.innovateuk.ifs.user.resource.UserStatus.INACTIVE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;

public class UserRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<UserRepository> {

    private final long INNOVATION_AREA_ID = 5L;

    @Override
    @Autowired
    protected void setRepository(UserRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private UserMapper userMapper;

    @Autowired
    protected ProfileRepository profileRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected InnovationAreaRepository innovationAreaRepository;

    @Autowired
    protected CompetitionInviteRepository competitionInviteRepository;

    @Test
    public void findAll() {
        // Fetch the list of users
        List<User> users = repository.findAll();
        assertEquals(USER_COUNT, users.size());

        // Assert that we've got the users we were expecting
        List<String> emailAddresses = users.stream().map(User::getEmail).collect(toList());
        List<String> expectedUsers = ALL_USERS_EMAIL;
        assertTrue(emailAddresses.containsAll(expectedUsers));
    }

    @Test
    public void findByEmailAndStatus() {
        final User user = newUser()
                .withUid("my-uid")
                .withStatus(INACTIVE)
                .build();

        final User expected = repository.save(user);

        final Optional<User> found = repository.findByEmailAndStatus(expected.getEmail(), INACTIVE);
        assertTrue(found.isPresent());
        assertEquals(expected, found.get());

        final Optional<User> shouldNotBeFound = repository.findByEmailAndStatus(expected.getEmail(), ACTIVE);
        assertFalse(shouldNotBeFound.isPresent());
    }

    @Test
    public void createUser() {
        loginSteveSmith();

        // Create a new user
        User newUser = repository.save(new User("New", "User", "new@example.com", "", "my-uid"));
        Profile profile = newProfile()
                .withId((Long) null)
                .withAddress(newAddress()
                        .withId((Long) null)
                        .withAddressLine1("Electric Works")
                        .build())
                .build();
        profileRepository.save(profile);
        newUser.setProfileId(profile.getId());
        assertNotNull(newUser.getId());

        // Fetch the list of users and assert that the count has increased and the new user is present in the list of expected users
        List<User> users = repository.findAll();
        assertEquals((USER_COUNT + 1), users.size());
        List<String> emailAddresses = users.stream().map(User::getEmail).collect(toList());
        List<String> expectedUsers = new ArrayList<>(ALL_USERS_EMAIL);
        expectedUsers.add("new@example.com");
        assertTrue(emailAddresses.containsAll(expectedUsers));

        User savedNewUser = repository.findByEmail("new@example.com").get();
        Profile savedProfile = profileRepository.findOne(savedNewUser.getProfileId());
        assertEquals("Electric Works", savedProfile.getAddress().getAddressLine1());
        assertEquals(userMapper.mapToDomain(getSteveSmith()), savedProfile.getCreatedBy());
        assertEquals(userMapper.mapToDomain(getSteveSmith()), savedProfile.getModifiedBy());
        assertNotNull(savedProfile.getModifiedBy());
    }

    @Test
    public void deleteNewUser() {
        // Create a new user
        User newUser = repository.save(new User("New", "User", "new@example.com", "", "my-uid"));

        // and immediately delete them
        repository.delete(newUser.getId());

        // Fetch the list of users and assert that the user doesn't exist in this list
        List<User> users = repository.findAll();
        assertEquals(USER_COUNT, users.size());
        List<String> emailAddresses = users.stream().map(User::getEmail).collect(toList());
        assertFalse(emailAddresses.contains("new@example.com"));
    }

    @Test
    public void findByIdAndRolesName() throws Exception {
        Optional<User> user = repository.findByIdAndRolesName(getPaulPlum().getId(), ASSESSOR.getName());

        assertTrue(user.isPresent());
    }

    @Test
    public void findByIdAndRolesName_wrongRole() throws Exception {
        Optional<User> user = repository.findByIdAndRolesName(getPaulPlum().getId(), COMP_ADMIN.getName());

        assertFalse(user.isPresent());
    }

    @Test
    public void findByRolesName() throws Exception {
        List<User> users = repository.findByRolesName(ASSESSOR.getName());

        assertEquals(2, users.size());
        assertEquals(getPaulPlum().getId(), users.get(0).getId());
        assertEquals(getFelixWilson().getId(), users.get(1).getId());
    }

    @Test
    public void findAssessorsByCompetition() throws Exception {
        long competitionId = 1L;

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();

        addTestAssessors();

        assertEquals(6, repository.findByRolesName(ASSESSOR.getName()).size());

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

    @Test
    public void findAssessorsByCompetition_nextPage() throws Exception {
        long competitionId = 1L;

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();

        addTestAssessors();

        assertEquals(6, repository.findByRolesName(ASSESSOR.getName()).size());

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
    public void findAssessorsByCompetitionAndInnovationArea() throws Exception {
        long competitionId = 1L;

        addTestAssessors();

        assertEquals(6, repository.findByRolesName(ASSESSOR.getName()).size());

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
    public void findAssessorsByCompetitionAndInnovationArea_noInnovationArea() throws Exception {
        long competitionId = 1L;
        Competition competition = newCompetition()
                .withId(competitionId)
                .build();

        addTestAssessors();

        assertEquals(6, repository.findByRolesName(ASSESSOR.getName()).size());

        Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "firstName"));

        saveInvite(competition, userMapper.mapToDomain(getPaulPlum()));
        saveInvite(competition, userMapper.mapToDomain(getFelixWilson()));

        Page<User> pagedUsers = repository.findAssessorsByCompetitionAndInnovationArea(competitionId, null, pageable);

        assertEquals(4, pagedUsers.getTotalElements());
        assertEquals(1, pagedUsers.getTotalPages());
        assertEquals(4, pagedUsers.getContent().size());
        assertEquals(0, pagedUsers.getNumber());
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

        repository.save(users);
        flushAndClearSession();
    }

    private void saveInvite(Competition competition, User user) {
        CompetitionInvite invite = new CompetitionInvite(user, CompetitionInvite.generateInviteHash(), competition);
        competitionInviteRepository.save(invite);
    }
}
