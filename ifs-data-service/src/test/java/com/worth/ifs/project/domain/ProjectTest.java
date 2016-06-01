package com.worth.ifs.project.domain;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class ProjectTest {
    Long id;
    Project project;
    LocalDate startDate;
    Address address;
    Long durationInMonths;
    ProcessRole projectManager;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        startDate = LocalDate.now();
        address = new Address();
        durationInMonths = 12L;
        projectManager = new ProcessRole();
        project = new Project(id, startDate, address, durationInMonths, projectManager);
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(project.getId(), id);
        Assert.assertEquals(project.getTargetStartDate(), startDate);
        Assert.assertEquals(project.getAddress(), address);
        Assert.assertEquals(project.getDurationInMonths(), durationInMonths);
        Assert.assertEquals(project.getProjectManager(), projectManager);
    }
}