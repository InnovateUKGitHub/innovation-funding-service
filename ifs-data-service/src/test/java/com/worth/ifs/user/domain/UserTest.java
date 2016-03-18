package com.worth.ifs.user.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
}