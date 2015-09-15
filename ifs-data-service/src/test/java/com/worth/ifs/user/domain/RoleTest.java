package com.worth.ifs.user.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RoleTest {
    Role role;

    List<ProcessRole> processRoles;
    Long id;
    String name;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "userApplicationRoleTestName";

        processRoles = new ArrayList<ProcessRole>();
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());

        role = new Role(id, name, processRoles);
    }

    @Test
    public void roleShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(role.getProcessRoles(), processRoles);
        Assert.assertEquals(role.getId(), id);
        Assert.assertEquals(role.getName(), name);
    }
}