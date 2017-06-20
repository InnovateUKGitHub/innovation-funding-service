package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;

public class ApplicationResourceTest {
    private ApplicationResource applicationResource;

    private Competition competition;
    private String name;
    private List<ProcessRole> processRoles;
    private ApplicationState applicationState;
    private Long id;
    private List<ApplicationFinance> applicationFinances;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "testApplicationName";
        applicationState = ApplicationState.OPEN;
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
                .withApplicationState(applicationState)
                .withId(id)
                .build();
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(applicationResource.getId(), id);
        Assert.assertEquals(applicationResource.getName(), name);
        Assert.assertEquals(applicationResource.getApplicationState(), applicationState);
        Assert.assertEquals(applicationResource.getCompetition(), competition.getId());
    }

    @Test
    public void equalsShouldReturnFalseOnNull() throws Exception {
        Assert.assertFalse(applicationResource.equals(null));
    }
}
