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
    public void userShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(user.getFirstName(), firstName);
        Assert.assertEquals(user.getLastName(), lastName);
        Assert.assertEquals(user.getName(), firstName + " " + lastName);
        Assert.assertEquals(user.getId(), id);
        Assert.assertEquals(user.getImageUrl(), imageUrl);
        Assert.assertEquals(user.getUid(), uid);
    }
}
