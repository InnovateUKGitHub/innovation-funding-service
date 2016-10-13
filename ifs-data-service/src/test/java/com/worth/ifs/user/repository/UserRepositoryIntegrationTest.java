package com.worth.ifs.user.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.user.builder.UserBuilder;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.user.builder.ProfileBuilder.newProfile;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.resource.UserStatus.ACTIVE;
import static com.worth.ifs.user.resource.UserStatus.INACTIVE;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class UserRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<UserRepository> {

    @Override
    @Autowired
    protected void setRepository(UserRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test_findAll() {
        // Fetch the list of users
        List<User> users = repository.findAll();
        assertEquals(USER_COUNT, users.size());

        // Assert that we've got the users we were expecting
        List<String> emailAddresses = users.stream().map(User::getEmail).collect(toList());
        List<String> expectedUsers = ALL_USERS_EMAIL;
        assertTrue(emailAddresses.containsAll(expectedUsers));
    }

    @Test
    @Rollback
    public void test_findByEmailAndStatus() {
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
    @Rollback
    public void test_createUser() {
        loginSteveSmith();

        // Create a new user
        User newUser = repository.save(new User("New", "User", "new@example.com", "", new ArrayList<>(), "my-uid"));
        newUser.setProfile(newProfile()
                .withId((Long)null)
                .withAddress(newAddress()
                        .withId((Long)null)
                        .withAddressLine1("Electric Works")
                        .build())
                .build());
        assertNotNull(newUser.getId());

        // Fetch the list of users and assert that the count has increased and the new user is present in the list of expected users
        List<User> users = repository.findAll();
        assertEquals((USER_COUNT+1), users.size());
        List<String> emailAddresses = users.stream().map(User::getEmail).collect(toList());
        List<String> expectedUsers = new ArrayList<>(ALL_USERS_EMAIL);
        expectedUsers.add("new@example.com");
        assertTrue(emailAddresses.containsAll(expectedUsers));

        User savedNewUser = repository.findByEmail("new@example.com").get();
        assertEquals("Electric Works", savedNewUser.getProfile().getAddress().getAddressLine1());
        assertEquals(userMapper.mapToDomain(getSteveSmith()), savedNewUser.getProfile().getCreatedBy());
        assertEquals(userMapper.mapToDomain(getSteveSmith()), savedNewUser.getProfile().getModifiedBy());
        assertNotNull(savedNewUser.getProfile().getModifiedBy());
    }

    @Test
    @Rollback
    public void test_deleteNewUser() {
        // Create a new user
        User newUser = repository.save(new User("New", "User", "new@example.com", "", new ArrayList<>(), "my-uid"));

        // and immediately delete them
        repository.delete(newUser.getId());

        // Fetch the list of users and assert that the user doesn't exist in this list
        List<User> users = repository.findAll();
        assertEquals(USER_COUNT, users.size());
        List<String> emailAddresses = users.stream().map(User::getEmail).collect(toList());
        assertFalse(emailAddresses.contains("new@example.com"));
    }
}
