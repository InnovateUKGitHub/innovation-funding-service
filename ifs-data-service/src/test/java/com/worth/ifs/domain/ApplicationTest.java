package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationTest {
    Application application;

    Competition competition;
    String name;
    List<UserApplicationRole> userApplicationRoles;
    ApplicationStatus applicationStatus;
    long id;

    @Before
    public void setUp() throws Exception {
        id =0L;
        name = "testApplicationName";
        applicationStatus = new ApplicationStatus();
        competition = new Competition();

        userApplicationRoles = new ArrayList<UserApplicationRole>();
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());

        application = new Application(competition, name, userApplicationRoles, applicationStatus, id);
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(application.getId(), id);
        Assert.assertEquals(application.getName(), name);
        Assert.assertEquals(application.getApplicationStatus(), applicationStatus);
        Assert.assertEquals(application.getUserApplicationRoles(), userApplicationRoles);
        Assert.assertEquals(application.getCompetition(), competition);
    }
}