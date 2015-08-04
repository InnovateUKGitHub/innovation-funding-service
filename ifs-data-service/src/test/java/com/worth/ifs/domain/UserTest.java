package com.worth.ifs.domain;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserTest {
    User user;

    List<UserApplicationRole> userApplicationRoles;
    long id;
    String name;
    String imageUrl;
    String token;


    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "testname";
        imageUrl = "/image/url/test";
        token = "testtoken123abc";

        userApplicationRoles = new ArrayList<UserApplicationRole>();
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());

        user = new User(id, name, imageUrl, token, userApplicationRoles);
    }

    @Test
    public void userShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(user.getUserApplicationRoles(), userApplicationRoles);
        Assert.assertEquals(user.getName(), name);
        Assert.assertEquals(user.getId(), id);
        Assert.assertEquals(user.getImageUrl(),imageUrl);
        Assert.assertEquals(user.getToken(), token);
    }
}