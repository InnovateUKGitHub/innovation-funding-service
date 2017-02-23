package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
import static org.junit.Assert.assertEquals;

public class InnovationSectorResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        long expectedId = 1L;
        String expectedName = "Health and life sciences";
        List<InnovationAreaResource> expectedChildren = newInnovationAreaResource().build(2);

        InnovationSectorResource innovationAreaResource = newInnovationSectorResource()
                .withId(expectedId)
                .withName(expectedName)
                .withChildren(expectedChildren)
                .build();

        assertEquals(expectedId, innovationAreaResource.getId().longValue());
        assertEquals(expectedName, innovationAreaResource.getName());
        assertEquals(expectedChildren, innovationAreaResource.getChildren());
        assertEquals(INNOVATION_SECTOR, innovationAreaResource.getType());
    }

    @Test
    public void buildMany() throws Exception {
        Long[] expectedIds = {1L, 2L};
        String[] expectedNames = {"Health and life sciences", "Materials and manufacturing"};
        List<InnovationAreaResource>[] expectedChildren = new List[]{
                newInnovationAreaResource().build(2),
                newInnovationAreaResource().build(2)
        };

        List<InnovationSectorResource> innovationSectorResources = newInnovationSectorResource()
                .withId(expectedIds)
                .withName(expectedNames)
                .withChildren(expectedChildren)
                .build(2);

        InnovationSectorResource first = innovationSectorResources.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedChildren[0], first.getChildren());
        assertEquals(INNOVATION_SECTOR, first.getType());

        InnovationSectorResource second = innovationSectorResources.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedChildren[1], second.getChildren());
        assertEquals(INNOVATION_SECTOR, second.getType());
    }
}
