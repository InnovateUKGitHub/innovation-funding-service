package org.innovateuk.ifs.user.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Sets.newHashSet;

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
