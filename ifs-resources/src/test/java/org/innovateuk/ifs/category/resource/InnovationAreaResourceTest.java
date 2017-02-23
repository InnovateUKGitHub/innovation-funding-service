package org.innovateuk.ifs.category.resource;

import org.junit.Test;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.junit.Assert.assertEquals;

public class InnovationAreaResourceTest {
    @Test
    public void getType() throws Exception {
        assertEquals(newInnovationAreaResource().build().getType(), INNOVATION_AREA);
    }
}