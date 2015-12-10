package com.worth.ifs.application.domain;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationTest {
    Application application;

    Competition competition;
    String name;
    List<ProcessRole> processRoles;
    ApplicationStatus applicationStatus;
    Long id;
    List<ApplicationFinance> applicationFinances;

    @Before
    public void setUp() throws Exception {
        id =0L;
        name = "testApplicationName";
        applicationStatus = new ApplicationStatus();
        competition = new Competition();
        applicationFinances = new ArrayList<>();

        processRoles = new ArrayList<>();
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());

        application = new Application(competition, name, processRoles, applicationStatus, id);
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(application.getId(), id);
        Assert.assertEquals(application.getName(), name);
        Assert.assertEquals(application.getApplicationStatus(), applicationStatus);
        Assert.assertEquals(application.getProcessRoles(), processRoles);
        Assert.assertEquals(application.getCompetition(), competition);
        Assert.assertEquals(application.getApplicationFinances(), applicationFinances);
    }
}