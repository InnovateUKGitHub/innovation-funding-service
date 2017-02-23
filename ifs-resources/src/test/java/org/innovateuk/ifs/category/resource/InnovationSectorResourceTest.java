package org.innovateuk.ifs.category.resource;

import org.junit.Test;

import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
import static org.junit.Assert.assertEquals;

public class InnovationSectorResourceTest {
    @Test
    public void getType() throws Exception {
        assertEquals(newInnovationSectorResource().build().getType(), INNOVATION_SECTOR);
    }
}