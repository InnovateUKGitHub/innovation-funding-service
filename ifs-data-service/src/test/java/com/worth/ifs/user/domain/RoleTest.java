package com.worth.ifs.user.domain;

import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserApplicationRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RoleTest {
    Role role;

    List<UserApplicationRole> userApplicationRoles;
    Long id;
    String name;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "userApplicationRoleTestName";

        userApplicationRoles = new ArrayList<UserApplicationRole>();
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());

        role = new Role(id, name, userApplicationRoles);
    }

    @Test
    public void roleShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(role.getUserApplicationRoles(), userApplicationRoles);
        Assert.assertEquals(role.getId(), id);
        Assert.assertEquals(role.getName(), name);
    }
}