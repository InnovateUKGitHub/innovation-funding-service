package com.worth.ifs.project.domain;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.application.domain.Application;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class ProjectTest {
    Long id;
    Application application;
    Project project;
    LocalDate startDate;
    Address address;
    Long durationInMonths;
    String name;
    LocalDateTime submittedDate;
    LocalDateTime documentsSubmittedDate;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        application = new Application();
        startDate = LocalDate.now();
        address = new Address();
        durationInMonths = 12L;
        name = "My Project";
        submittedDate = LocalDateTime.now();
        project = new Project(id, application, startDate, address, durationInMonths, name, submittedDate, documentsSubmittedDate);
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() throws Exception {
        assertEquals(project.getId(), id);
        assertEquals(project.getApplication(), application);
        assertEquals(project.getTargetStartDate(), startDate);
        assertEquals(project.getAddress(), address);
        assertEquals(project.getDurationInMonths(), durationInMonths);
        assertEquals(project.getName(), name);
        assertEquals(project.getSubmittedDate(), submittedDate);
        assertEquals(project.getDocumentsSubmittedDate(), documentsSubmittedDate);
    }
}