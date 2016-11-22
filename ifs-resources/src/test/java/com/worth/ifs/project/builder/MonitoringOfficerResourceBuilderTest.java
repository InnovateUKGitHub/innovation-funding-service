package com.worth.ifs.project.builder;

import com.worth.ifs.commons.test.BaseTest;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.junit.Assert.assertEquals;

/**
 * Test the basic functions of the builder
 */
public class MonitoringOfficerResourceBuilderTest extends BaseTest {

    @Test
    public void testUniqueFields() {

        List<MonitoringOfficerResource> mos = MonitoringOfficerResourceBuilder.newMonitoringOfficerResource().withProject(123L, 456L).build(2);

        MonitoringOfficerResource mo1 = mos.get(0);
        assertEquals(Long.valueOf(1), mo1.getId());
        assertEquals(Long.valueOf(123), mo1.getProject());
        assertEquals("Monitoring 1", mo1.getFirstName());
        assertEquals("Officer 1", mo1.getLastName());
        assertEquals("mo1@example.com", mo1.getEmail());
        assertEquals("1 9999", mo1.getPhoneNumber());

        MonitoringOfficerResource mo2 = mos.get(1);
        assertEquals(Long.valueOf(2), mo2.getId());
        assertEquals(Long.valueOf(456), mo2.getProject());
        assertEquals("Monitoring 2", mo2.getFirstName());
        assertEquals("Officer 2", mo2.getLastName());
        assertEquals("mo2@example.com", mo2.getEmail());
        assertEquals("2 9999", mo2.getPhoneNumber());
    }
}
