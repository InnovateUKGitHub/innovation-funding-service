package com.worth.ifs.user.builder;

import com.worth.ifs.user.domain.Ethnicity;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EthnicityBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedName = "WHITE";
        String expectedDescription = "White";
        int expectedPriority = 3;
        boolean expectedActive = false;

        Ethnicity ethnicity = EthnicityBuilder.newEthnicity()
                .withId(expectedId)
                .withName(expectedName)
                .withDescription(expectedDescription)
                .withPriority(expectedPriority)
                .withActive(expectedActive)
                .build();

        assertEquals(expectedId, ethnicity.getId());
        assertEquals(expectedName, ethnicity.getName());
        assertEquals(expectedDescription, ethnicity.getDescription());
        assertEquals(expectedPriority, ethnicity.getPriority());
        assertEquals(expectedActive, ethnicity.isActive());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 1L, 2L };
        String[] expectedNames = { "WHITE", "BLACK" };
        String[] expectedDescriptions = { "White", "Black" };
        Integer[] expectedPriorities = { 3, 5 };
        Boolean[] expectedActives = { false, true };

        List<Ethnicity> ethnicities = EthnicityBuilder.newEthnicity()
                .withId(expectedIds)
                .withName(expectedNames)
                .withDescription(expectedDescriptions)
                .withPriority(expectedPriorities)
                .withActive(expectedActives)
                .build(2);

        Ethnicity first = ethnicities.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedDescriptions[0], first.getDescription());
        assertEquals(expectedPriorities[0], (Integer) first.getPriority());
        assertEquals(expectedActives[0], first.isActive());

        Ethnicity second = ethnicities.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedDescriptions[1], second.getDescription());
        assertEquals(expectedPriorities[1], (Integer) second.getPriority());
        assertEquals(expectedActives[1], second.isActive());
    }
}