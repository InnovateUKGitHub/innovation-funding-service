package com.worth.ifs.application.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ApplicationStatusTest {
    ApplicationStatus applicationStatus;

    Long id;
    String name;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        name="testProcessStatusName";

        applicationStatus = new ApplicationStatus(id, name);
    }

    @Test
    public void processStatusShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(applicationStatus.getId(), id);
        Assert.assertEquals(applicationStatus.getName(), name);
    }
}