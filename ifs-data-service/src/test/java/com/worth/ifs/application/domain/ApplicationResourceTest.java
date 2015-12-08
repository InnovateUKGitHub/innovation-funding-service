package com.worth.ifs.application.domain;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationResourceTest {
    ApplicationResource applicationResource;

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

        processRoles = new ArrayList<ProcessRole>();
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());

        applicationResource = new ApplicationResource(competition, name, processRoles, applicationStatus, id);
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(applicationResource.getId(), id);
        Assert.assertEquals(applicationResource.getName(), name);
        Assert.assertEquals(applicationResource.getApplicationStatus(), applicationStatus);
        Assert.assertEquals(applicationResource.getProcessRoles(), processRoles);
        Assert.assertEquals(applicationResource.getCompetition(), competition);
        Assert.assertEquals(applicationResource.getApplicationFinances(), applicationFinances);
    }
}