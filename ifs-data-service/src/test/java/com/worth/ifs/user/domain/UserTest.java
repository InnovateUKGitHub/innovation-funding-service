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
    String name;
    String imageUrl;
    String token;
    String password;
    String email;
    String uid;


    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "testname";
        email = "test@innovateuk.org";
        password = "test123";
        imageUrl = "/image/url/test";
        token = "testtoken123abc";
        uid = "uid";

        processRoles = new ArrayList<>();
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());

        user = new User(id, name, email, password, token, imageUrl, processRoles, "uid");
    }

    @Test
    public void userShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(user.getProcessRoles(), processRoles);
        Assert.assertEquals(user.getName(), name);
        Assert.assertEquals(user.getId(), id);
        Assert.assertEquals(user.getImageUrl(),imageUrl);
        Assert.assertEquals(user.getToken(), token);
        Assert.assertEquals(user.getUid(), uid);
    }
}