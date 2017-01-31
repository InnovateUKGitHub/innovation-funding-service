package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.junit.Assert.assertEquals;

public class InnovationAreaBuilderTest {
    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedName = "Internet of Things";
        InnovationSector expectedParent = newInnovationSector().build();

        InnovationArea innovationArea = newInnovationArea()
                .withId(expectedId)
                .withName(expectedName)
                .withSector(expectedParent)
                .build();

        assertEquals(expectedId, innovationArea.getId());
        assertEquals(expectedName, innovationArea.getName());
        assertEquals(expectedParent, innovationArea.getSector());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 7L, 13L };
        String[] expectedNames = { "Internet of Things", "Manufacturing" };
        InnovationSector[] expectedParents = newInnovationSector().buildArray(2, InnovationSector.class);

        List<InnovationArea> innovationAreas = newInnovationArea()
                .withId(expectedIds)
                .withName(expectedNames)
                .withSector(expectedParents)
                .build(2);

        InnovationArea first = innovationAreas.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedParents[0], first.getSector());

        InnovationArea second = innovationAreas.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedParents[1], second.getSector());
    }
}
