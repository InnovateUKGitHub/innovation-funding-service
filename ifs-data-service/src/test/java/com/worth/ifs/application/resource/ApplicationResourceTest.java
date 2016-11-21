package com.worth.ifs.application.resource;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;

public class ApplicationResourceTest {
    ApplicationResource applicationResource;

    Competition competition;
    String name;
    List<ProcessRole> processRoles;
    ApplicationStatusConstants applicationStatus;
    Long id;
    List<ApplicationFinance> applicationFinances;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "testApplicationName";
        applicationStatus = ApplicationStatusConstants.OPEN;
        competition = new Competition();
        competition.setId(1L);

        applicationFinances = new ArrayList<>();
        applicationFinances.add(new ApplicationFinance(1L, null, null));
        applicationFinances.add(new ApplicationFinance(2L, null, null));
        applicationFinances.add(new ApplicationFinance(3L, null, null));

        processRoles = new ArrayList<>();
        processRoles.add(new ProcessRole(1L, null, null, null, null));
        processRoles.add(new ProcessRole(2L, null, null, null, null));
        processRoles.add(new ProcessRole(3L, null, null, null, null));

        applicationResource = newApplicationResource()
                .withCompetition(competition.getId())
                .withName(name)
                .withApplicationStatus(applicationStatus)
                .withId(id)
                .build();
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(applicationResource.getId(), id);
        Assert.assertEquals(applicationResource.getName(), name);
        Assert.assertEquals(applicationResource.getApplicationStatus(), applicationStatus.getId());
        Assert.assertEquals(applicationResource.getCompetition(), competition.getId());
    }

    @Test
    public void equalsShouldReturnFalseOnNull() throws Exception {
        Assert.assertFalse(applicationResource.equals(null));
    }
}