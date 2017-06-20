package org.innovateuk.ifs.user.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RoleTest {
    Role role;

    Long id;
    String name;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "userApplicationRoleTestName";
        role = new Role(id, name);
    }

    @Test
    public void roleShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(role.getId(), id);
        Assert.assertEquals(role.getName(), name);
    }
}
