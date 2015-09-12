package com.worth.ifs.user.domain;

import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserApplicationRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserTest {
    User user;

    List<UserApplicationRole> userApplicationRoles;
    Long id;
    String name;
    String imageUrl;
    String token;
    String password;
    String email;


    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "testname";
        email = "test@innovateuk.org";
        password = "test123";
        imageUrl = "/image/url/test";
        token = "testtoken123abc";

        userApplicationRoles = new ArrayList<UserApplicationRole>();
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());

        user = new User(id, name, email, password, token, imageUrl, userApplicationRoles);
    }

    @Test
    public void userShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(user.getUserApplicationRoles(), userApplicationRoles);
        Assert.assertEquals(user.getName(), name);
        Assert.assertEquals(user.getId(), id);
        Assert.assertEquals(user.getImageUrl(),imageUrl);
        Assert.assertEquals(user.getToken(), token);
        Assert.assertEquals(user.getToken(), token);
    }
}