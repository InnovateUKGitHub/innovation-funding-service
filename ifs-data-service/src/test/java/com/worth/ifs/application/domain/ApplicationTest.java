package com.worth.ifs.application.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.UserApplicationRole;
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
    Long id;

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