package com.worth.ifs.application.domain;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.UserApplicationRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class OrganisationTest {
    Organisation organisation;

    long id;
    String name;
    List<UserApplicationRole> userApplicationRoles;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "test organisation";
        userApplicationRoles = new ArrayList<UserApplicationRole>();
        organisation = new Organisation(id, name);
    }

    @Test
    public void organisationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(organisation.getId(), id);
        Assert.assertEquals(organisation.getName(), name);
        Assert.assertEquals(organisation.getUserApplicationRoles(), userApplicationRoles);
    }
}