package com.worth.ifs.application.resource;

import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        processRoles = new ArrayList<>();
        processRoles.add(new ProcessRole(1L,null,null,null,null));
        processRoles.add(new ProcessRole(2L,null,null,null,null));
        processRoles.add(new ProcessRole(3L,null,null,null,null));

        applicationResource = new ApplicationResource(competition, name, processRoles, applicationStatus, id);
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(applicationResource.getId(), id);
        Assert.assertEquals(applicationResource.getName(), name);
        Assert.assertEquals(applicationResource.getApplicationStatus(), applicationStatus);
        Assert.assertEquals(applicationResource.getProcessRoleIds(), processRoles.stream().map(ProcessRole::getId).collect(Collectors.toList()));
        Assert.assertEquals(applicationResource.getCompetitionId(), competition.getId());
        Assert.assertEquals(applicationResource.getApplicationFinances(), applicationFinances);
    }

    @Test
    public void equalsShouldReturnFalseOnNull() throws Exception{
        Assert.assertFalse(applicationResource.equals(null));
    }
}