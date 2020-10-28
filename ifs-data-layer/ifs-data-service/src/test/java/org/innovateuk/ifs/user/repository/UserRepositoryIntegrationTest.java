package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.innovateuk.ifs.user.resource.UserStatus.INACTIVE;
import static org.junit.Assert.*;

public class UserRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<UserRepository> {


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
    protected InnovationAreaRepository innovationAreaRepository;

    @Test
    public void findByRolesInAndStatusIn() {
        EnumSet<Role> roles = EnumSet.of(APPLICANT, ASSESSOR);
        EnumSet<UserStatus> statuses = EnumSet.of(ACTIVE, INACTIVE);

        List<User> users = repository.findDistinctByRolesInAndStatusIn(roles, statuses);

        assertTrue(users.stream()
                .flatMap(user -> user.getRoles().stream())
                .allMatch(roles::contains));

        assertTrue(users.stream()
                .map(User::getStatus)
                .allMatch(statuses::contains));
    }

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
        loginSteveSmith();
        User creator = repository.findById(getLoggedInUser().getId()).get();
        repository.findById(getLoggedInUser().getId());
        final User user = newUser()
                .withUid("my-uid")
                .withStatus(INACTIVE)
                .withCreatedOn(ZonedDateTime.now())
                .withCreatedBy(creator)
                .withModifiedOn(ZonedDateTime.now())
                .withModifiedBy(creator)
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
        Profile savedProfile = profileRepository.findById(savedNewUser.getProfileId()).get();
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
        repository.deleteById(newUser.getId());

        // Fetch the list of users and assert that the user doesn't exist in this list
        List<User> users = repository.findAll();
        assertEquals(USER_COUNT, users.size());
        List<String> emailAddresses = users.stream().map(User::getEmail).collect(toList());
        assertFalse(emailAddresses.contains("new@example.com"));
    }

    @Test
    public void findByIdAndRolesName() {
        Optional<User> user = repository.findByIdAndRoles(getPaulPlum().getId(), ASSESSOR);

        assertTrue(user.isPresent());
    }

    @Test
    public void findByIdAndRolesName_wrongRole() {
        Optional<User> user = repository.findByIdAndRoles(getPaulPlum().getId(), COMP_ADMIN);

        assertFalse(user.isPresent());
    }

    @Test
    public void findByRolesName() {
        List<User> users = repository.findByRolesAndStatusIn(ASSESSOR, EnumSet.allOf(UserStatus.class));

        assertEquals(2, users.size());
        assertEquals(getPaulPlum().getId(), users.get(0).getId());
        assertEquals(getFelixWilson().getId(), users.get(1).getId());
    }

    @Test
    public void findByRolesNameOrderByFirstNameAscLastNameAsc() {
        List<User> users = repository.findByRolesOrderByFirstNameAscLastNameAsc(ASSESSOR);

        assertEquals(2, users.size());
        assertEquals(getFelixWilson().getId(), users.get(0).getId());
        assertEquals(getPaulPlum().getId(), users.get(1).getId());
    }
}
