package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProcessStatusTest {
    ProcessStatus processStatus;

    long id;
    String name;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        name="testProcessStatusName";

        processStatus = new ProcessStatus(id, name);
    }

    @Test
    public void processStatusShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(processStatus.getId(), id);
        Assert.assertEquals(processStatus.getName(), name);
    }
}