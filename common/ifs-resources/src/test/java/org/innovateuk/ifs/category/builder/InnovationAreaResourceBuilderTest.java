package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.junit.Assert.assertEquals;

public class InnovationAreaResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        long expectedId = 1L;
        String expectedName = "Test Category";
        long expectedSectorId = 2L;
        String expectedSectorName = "Test Sector";

        InnovationAreaResource innovationAreaResource = newInnovationAreaResource()
                .withId(expectedId)
                .withName(expectedName)
                .withSector(expectedSectorId)
                .withSectorName(expectedSectorName)
                .build();

        assertEquals(expectedId, innovationAreaResource.getId().longValue());
        assertEquals(expectedName, innovationAreaResource.getName());
        assertEquals(expectedSectorId, innovationAreaResource.getSector().longValue());
        assertEquals(expectedSectorName, innovationAreaResource.getSectorName());
        assertEquals(INNOVATION_AREA, innovationAreaResource.getType());
    }

    @Test
    public void buildMany() throws Exception {
        Long[] expectedIds = {1L, 2L};
        String[] expectedNames = {"Innovation Area 1", "Innovation Area 2"};
        Long[] expectedSectorIds = {2L, 0L};
        String[] expectedSectorNames = {"Test Sector 1", "Test Sector 2"};

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withSector(expectedSectorIds)
                .withSectorName(expectedSectorNames)
                .build(2);

        InnovationAreaResource first = innovationAreaResources.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedSectorIds[0], first.getSector());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedSectorNames[0], first.getSectorName());
        assertEquals(INNOVATION_AREA, first.getType());

        InnovationAreaResource second = innovationAreaResources.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedSectorIds[1], second.getSector());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedSectorNames[1], second.getSectorName());
        assertEquals(INNOVATION_AREA, second.getType());
    }
}
