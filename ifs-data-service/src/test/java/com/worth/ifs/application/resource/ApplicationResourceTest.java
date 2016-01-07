package com.worth.ifs.application.resource;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.ApplicationStatusResourceBuilder.newApplicationStatusResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

public class ApplicationResourceTest {
    ApplicationResource applicationResource;

    Competition competition;
    String name;
    List<ProcessRole> processRoles;
    ApplicationStatusResource applicationStatus;
    Long id;
    List<ApplicationFinance> applicationFinances;

    @Before
    public void setUp() throws Exception {
        id =0L;
        name = "testApplicationName";
        applicationStatus = newApplicationStatusResource().with(application -> application.setId(id)).withName("status").build();
        competition = new Competition();
        competition.setId(1L);

        applicationFinances = new ArrayList<>();
        applicationFinances.add(new ApplicationFinance(1L, null, null));
        applicationFinances.add(new ApplicationFinance(2L, null, null));
        applicationFinances.add(new ApplicationFinance(3L, null, null));

        processRoles = new ArrayList<>();
        processRoles.add(new ProcessRole(1L,null,null,null,null));
        processRoles.add(new ProcessRole(2L,null,null,null,null));
        processRoles.add(new ProcessRole(3L,null,null,null,null));

        applicationResource = newApplicationResource()
            .withCompetition(competition)
            .withName(name)
            .withProcessRoles(
                new ProcessRole(1L,null,null,null,null),
                new ProcessRole(2L,null,null,null,null),
                new ProcessRole(3L,null,null,null,null)
            )
            .withApplicationStatus(applicationStatus)
            .withId(id)
            .build();
        applicationResource.setApplicationFinances(simpleMap(applicationFinances,ApplicationFinance::getId));
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(applicationResource.getId(), id);
        Assert.assertEquals(applicationResource.getName(), name);
        Assert.assertEquals(applicationResource.getApplicationStatus(), applicationStatus.getId());
        Assert.assertEquals(applicationResource.getProcessRoles(), simpleMap(processRoles, ProcessRole::getId));
        Assert.assertEquals(applicationResource.getCompetition(), competition.getId());
        Assert.assertEquals(applicationResource.getApplicationFinances(), simpleMap(applicationFinances, ApplicationFinance::getId));
    }

    @Test
    public void equalsShouldReturnFalseOnNull() throws Exception{
        Assert.assertFalse(applicationResource.equals(null));
    }
}