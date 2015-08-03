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
    @Before
    public void setUp() throws Exception {
        List<UserApplicationRole> userApplicationRoles = new ArrayList<UserApplicationRole>();
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());

        user = new User(0, "testname", "/image/url/test", "testtoken123abc", userApplicationRoles);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void userShouldHaveCorrectUserApplicationRoleListSize() throws Exception {
        Assert.assertEquals(user.getUserApplicationRoles().size(), 3);
    }

    @Test
    public void userShouldHaveName() throws Exception {
        Assert.assertEquals(user.getName(), "testname");
    }

    @Test
    public void userShouldHaveId() throws Exception {
        Assert.assertEquals(user.getId(), 0);
    }

    @Test
    public void userShouldHaveImageUrl() throws Exception {
        Assert.assertEquals(user.getImageUrl(),"/image/url/test");
    }

    @Test
    public void userShouldHaveToken() throws Exception {
        Assert.assertEquals(user.getToken(),"testtoken123abc");
    }
}