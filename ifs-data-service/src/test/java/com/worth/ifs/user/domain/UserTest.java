package com.worth.ifs.user.domain;

import com.worth.ifs.user.builder.UserBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UserTest {
    User user;

    List<ProcessRole> processRoles;
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

        processRoles = new ArrayList<>();
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());

        user = new User(id, firstName, lastName, email, imageUrl, processRoles, "uid");
    }

    @Test
    public void userShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(user.getProcessRoles(), processRoles);
        Assert.assertEquals(user.getFirstName(), firstName);
        Assert.assertEquals(user.getLastName(), lastName);
        Assert.assertEquals(user.getName(), firstName + " " + lastName);
        Assert.assertEquals(user.getId(), id);
        Assert.assertEquals(user.getImageUrl(),imageUrl);
        Assert.assertEquals(user.getUid(), uid);
    }



    @Test
    public void testAddOrganisations() {
        Organisation org1 = newOrganisation().build();
        Organisation org2 = newOrganisation().build();
        User u = UserBuilder.newUser().build();
        u.setOrganisations(new ArrayList<>(asList(org1, org2))); // Not using the builder as that uses method under test
        // Check everything is as expected before testing
        assertNotNull(u.getOrganisations());
        assertEquals(2, u.getOrganisations().size());
        assertTrue(u.getOrganisations().contains(org1));
        assertTrue(u.getOrganisations().contains(org2));

        // Add an org already existing - nothing should change
        u.addUserOrganisation(org1);
        assertNotNull(u.getOrganisations());
        assertEquals(2, u.getOrganisations().size());
        assertTrue(u.getOrganisations().contains(org1));
        assertTrue(u.getOrganisations().contains(org2));

        // Add an new org
        Organisation org3 = newOrganisation().build();
        u.addUserOrganisation(org3);
        assertNotNull(u.getOrganisations());
        assertEquals(3, u.getOrganisations().size());
        assertTrue(u.getOrganisations().contains(org1));
        assertTrue(u.getOrganisations().contains(org2));
        assertTrue(u.getOrganisations().contains(org3));
    }

}