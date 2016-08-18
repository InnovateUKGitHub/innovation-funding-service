package com.worth.ifs.user.builder;

import com.worth.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

/**
 * Test the initial setup of the builder
 */
public class UserBuilderTest {

    @Test
    public void testUsersBuiltWithInitialSetOfValuesDefined() {

        List<User> users = newUser().build(2);
        Long initialId = users.get(0).getId();

        assertEquals(Long.valueOf(initialId), users.get(0).getId());
        assertEquals(Long.valueOf(initialId + 1), users.get(1).getId());

        assertEquals("User " + initialId, users.get(0).getName());
        assertEquals("User " + (initialId + 1), users.get(1).getName());

        assertEquals("user" + initialId + "@example.com", users.get(0).getEmail());
        assertEquals("user" + (initialId + 1) + "@example.com", users.get(1).getEmail());
    }
}
