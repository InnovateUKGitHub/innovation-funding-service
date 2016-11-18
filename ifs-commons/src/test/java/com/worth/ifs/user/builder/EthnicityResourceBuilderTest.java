package com.worth.ifs.user.builder;

import com.worth.ifs.user.resource.EthnicityResource;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EthnicityResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedName = "WHITE";
        String expectedDescription = "White";
        int expectedPriority = 3;

        EthnicityResource ethnicity = EthnicityResourceBuilder.newEthnicityResource()
                .withId(expectedId)
                .withName(expectedName)
                .withDescription(expectedDescription)
                .withPriority(expectedPriority)
                .build();

        assertEquals(expectedId, ethnicity.getId());
        assertEquals(expectedName, ethnicity.getName());
        assertEquals(expectedDescription, ethnicity.getDescription());
        assertEquals(expectedPriority, ethnicity.getPriority());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 1L, 2L };
        String[] expectedNames = { "WHITE", "BLACK" };
        String[] expectedDescriptions = { "White", "Black" };
        Integer[] expectedPriorities = { 3, 5 };

        List<EthnicityResource> ethnicities = EthnicityResourceBuilder.newEthnicityResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withDescription(expectedDescriptions)
                .withPriority(expectedPriorities)
                .build(2);

        EthnicityResource first = ethnicities.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedDescriptions[0], first.getDescription());
        assertEquals(expectedPriorities[0], (Integer) first.getPriority());

        EthnicityResource second = ethnicities.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedDescriptions[1], second.getDescription());
        assertEquals(expectedPriorities[1], (Integer) second.getPriority());
    }
}