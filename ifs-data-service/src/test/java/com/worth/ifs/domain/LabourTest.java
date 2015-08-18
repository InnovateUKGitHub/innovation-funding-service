package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LabourTest {
    Labour labour;
    Long id;
    String projectRole;
    Double grossAnnualSalary;
    Integer labourDays;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        projectRole = "project role";
        grossAnnualSalary = 2200000d;
        labourDays = 10;

        labour = new Labour(id, projectRole, grossAnnualSalary, labourDays);
    }

    @Test
    public void labourShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(labour.getId(), id);
        Assert.assertEquals(labour.getProjectRole(), projectRole);
        Assert.assertEquals(labour.getGrossAnnualSalary(), grossAnnualSalary);
        Assert.assertEquals(labour.getLabourDays(), labourDays);
    }
}