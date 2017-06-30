package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.junit.Assert.assertEquals;

public class ResearchCategoryBuilderTest {
    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedName = "Feasibility studies";

        ResearchCategory innovationSector = newResearchCategory()
                .withId(expectedId)
                .withName(expectedName)
                .build();

        assertEquals(expectedId, innovationSector.getId());
        assertEquals(expectedName, innovationSector.getName());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 7L, 13L };
        String[] expectedNames = { "Feasibility studies", "Industrial research" };

        List<ResearchCategory> innovationSectors = newResearchCategory()
                .withId(expectedIds)
                .withName(expectedNames)
                .build(2);

        ResearchCategory first = innovationSectors.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());

        ResearchCategory second = innovationSectors.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
    }
}
