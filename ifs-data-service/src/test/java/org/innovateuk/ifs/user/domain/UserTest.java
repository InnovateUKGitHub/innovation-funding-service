package org.innovateuk.ifs.user.domain;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;

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
        Assert.assertEquals(user.getImageUrl(), imageUrl);
        Assert.assertEquals(user.getUid(), uid);
    }


    @Test
    public void testAddOrganisations() {
        Organisation org1 = newOrganisation().build();
        Organisation org2 = newOrganisation().build();
        User u = newUser().build();
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

    @Test
    public void testAddInnovationAreas() throws Exception {
        InnovationArea expectedInnovationArea1 = newInnovationArea().withName("Innovation Area A").build();
        InnovationArea expectedInnovationArea2 = newInnovationArea().withName("Innovation Area B").build();

        User user = newUser().build();

        user.addInnovationAreas(newHashSet(expectedInnovationArea1, expectedInnovationArea2));

        Set<InnovationArea> innovationAreas = user.getInnovationAreas();

        assertEquals(2, innovationAreas.size());
        assertTrue(innovationAreas.contains(expectedInnovationArea1));
        assertTrue(innovationAreas.contains(expectedInnovationArea2));
    }
}
