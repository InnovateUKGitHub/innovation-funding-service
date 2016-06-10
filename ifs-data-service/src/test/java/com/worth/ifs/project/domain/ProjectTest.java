package com.worth.ifs.project.domain;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class ProjectTest {
    Long id;
    Application application;
    Project project;
    LocalDate startDate;
    Address address;
    Long durationInMonths;
    ProcessRole projectManager;
    String name;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        application = new Application();
        startDate = LocalDate.now();
        address = new Address();
        durationInMonths = 12L;
        projectManager = new ProcessRole();
        name = "My Project";
        project = new Project(id, application, startDate, address, durationInMonths, projectManager, name);
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        assertEquals(project.getId(), id);
        assertEquals(project.getApplication(), application);
        assertEquals(project.getTargetStartDate(), startDate);
        assertEquals(project.getAddress(), address);
        assertEquals(project.getDurationInMonths(), durationInMonths);
        assertEquals(project.getProjectManager(), projectManager);
        assertEquals(project.getName(), name);
    }
}