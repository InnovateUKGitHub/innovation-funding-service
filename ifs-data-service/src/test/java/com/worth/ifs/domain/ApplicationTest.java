package com.worth.ifs.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ApplicationTest {
    Application application;

    Competition competition;
    String name;
    List<UserApplicationRole> userApplicationRoles;
    ProcessStatus processStatus;
    long id;

    @Before
    public void setUp() throws Exception {
        id =0L;
        name = "testApplicationName";
        processStatus = new ProcessStatus();
        competition = new Competition();

        userApplicationRoles = new ArrayList<UserApplicationRole>();
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());
        userApplicationRoles.add(new UserApplicationRole());

        application = new Application(competition, name, userApplicationRoles, processStatus, id);
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(application.getId(), id);
        Assert.assertEquals(application.getName(), name);
        Assert.assertEquals(application.getProcessStatus(),processStatus);
        Assert.assertEquals(application.getUserApplicationRoles(), userApplicationRoles);
        Assert.assertEquals(application.getCompetition(), competition);

    }
}