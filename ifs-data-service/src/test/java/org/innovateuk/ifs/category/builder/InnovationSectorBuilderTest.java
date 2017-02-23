package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.junit.Assert.assertEquals;

public class InnovationSectorBuilderTest {
    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedName = "Health and life sciences";
        List<InnovationArea> expectedChildren = newInnovationArea().build(2);

        InnovationSector innovationSector = newInnovationSector()
                .withId(expectedId)
                .withName(expectedName)
                .withChildren(expectedChildren)
                .build();

        assertEquals(expectedId, innovationSector.getId());
        assertEquals(expectedName, innovationSector.getName());
        assertEquals(expectedChildren, innovationSector.getChildren());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 7L, 13L };
        String[] expectedNames = { "Health and life sciences", "Materials and manufacturing" };
        List<InnovationArea>[] expectedChildren = new List[]{newInnovationArea().build(2), newInnovationArea().build(2)};

        List<InnovationSector> innovationSectors = newInnovationSector()
                .withId(expectedIds)
                .withName(expectedNames)
                .withChildren(expectedChildren)
                .build(2);

        InnovationSector first = innovationSectors.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedChildren[0], first.getChildren());

        InnovationSector second = innovationSectors.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedChildren[1], second.getChildren());
    }
}
