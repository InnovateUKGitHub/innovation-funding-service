package org.innovateuk.ifs.user.repository;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.user.domain.Profile;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static freemarker.template.utility.Collections12.singletonList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
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
        Optional<User> user = repository.findByIdAndRolesName(3L, ASSESSOR.getName());

        assertTrue(user.isPresent());
    }

    @Test
    public void findByIdAndRolesName_wrongRole() throws Exception {
        Optional<User> user = repository.findByIdAndRolesName(3L, COMP_ADMIN.getName());

        assertFalse(user.isPresent());
    }

    @Test
    public void findByRolesNameAndIdNotIn() throws Exception {
        addTestAssessors();

        assertEquals(6, repository.findByRoles_Name(ASSESSOR.getName()).size());

        Collection<Long> userIds = asList(getPaulPlum().getId(), getFelixWilson().getId());

        Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "firstName"));

        Page<User> pagedUsers = repository.findByRolesNameAndIdNotIn(ASSESSOR.getName(), userIds, pageable);

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
    public void findByRolesNameAndIdNotIn_nextPage() throws Exception {
        addTestAssessors();

        assertEquals(6, repository.findByRoles_Name(ASSESSOR.getName()).size());

        Pageable pageable = new PageRequest(1, 2, new Sort(Sort.Direction.ASC, "firstName"));

        Collection<Long> userIds = asList(getPaulPlum().getId(), getFelixWilson().getId());
        Page<User> pagedUsers = repository.findByRolesNameAndIdNotIn(ASSESSOR.getName(), userIds, pageable);

        assertEquals(4, pagedUsers.getTotalElements());
        assertEquals(2, pagedUsers.getTotalPages());
        assertEquals(2, pagedUsers.getContent().size());
        assertEquals(1, pagedUsers.getNumber());
        assertEquals("Jessica", pagedUsers.getContent().get(0).getFirstName());
        assertEquals("Victoria", pagedUsers.getContent().get(1).getFirstName());
    }

    @Test
    public void findByRolesNameAndIdNotInAndProfileInnovationArea() throws Exception {
        addTestAssessors();

        assertEquals(6, repository.findByRoles_Name(ASSESSOR.getName()).size());

        Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "firstName"));

        Collection<Long> userIds = asList(getPaulPlum().getId(), getFelixWilson().getId());
        Page<User> pagedUsers = repository.findByRolesNameAndIdNotInAndProfileInnovationArea(ASSESSOR.getName(), userIds, INNOVATION_AREA_ID, pageable);

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
                .withRoles(singletonList(assessorRole))
                .withProfileId(profileIds[0], profileIds[1], profileIds[2], profileIds[3])
                .build(4);

        repository.save(users);
        flushAndClearSession();
    }
}
