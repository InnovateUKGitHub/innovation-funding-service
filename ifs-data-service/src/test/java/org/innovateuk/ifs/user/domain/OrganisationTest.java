package org.innovateuk.ifs.user.domain;

import org.innovateuk.ifs.user.builder.UserBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.*;

public class OrganisationTest {

    User user;

    Long id;
    String imageUrl;
    String email;
    String uid;
    private String firstName;
    private String lastName;


    @Before
    public void setUp() throws Exception {
        id = 0L;
        firstName = "firstName";
        lastName = "lastName";
        email = "test@innovateuk.org";
        imageUrl = "/image/url/test";
        uid = "uid";

        user = new User(id, firstName, lastName, email, imageUrl, uid);
    }

    @Test
    public void testAddOrganisations() {
        Organisation o1 = newOrganisation().build();
        Organisation o2 = newOrganisation().build();
        User u1 = UserBuilder.newUser().build();
        User u2 = UserBuilder.newUser().build();

        o1.setUsers(new ArrayList<>(asList(u1)));
        o2.setUsers(new ArrayList<>(asList(u1, u2)));

        // Check everything is as expected before making changes
        assertNotNull(o1.getUsers());
        assertEquals(1, o1.getUsers().size());
        assertEquals(2, o2.getUsers().size());
        assertTrue(o1.getUsers().contains(u1));
        assertTrue(o2.getUsers().contains(u1));
        assertTrue(o2.getUsers().contains(u2));

        // Add existing user to an org - nothing should change
        o1.addUser(u1);
        assertNotNull(o1.getUsers());
        assertEquals(1, o1.getUsers().size());
        assertTrue(o1.getUsers().contains(u1));

        // Add a new user
        User u3 = UserBuilder.newUser().build();
        o2.addUser(u3);
        assertNotNull(o2.getUsers());
        assertEquals(3, o2.getUsers().size());
        assertTrue(o2.getUsers().contains(u1));
        assertTrue(o2.getUsers().contains(u2));
        assertTrue(o2.getUsers().contains(u3));
    }

}
