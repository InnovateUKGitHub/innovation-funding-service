package org.innovateuk.ifs.category.resource;

import org.junit.Test;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InnovationAreaResourceTest {
    @Test
    public void getType() {
        assertEquals(newInnovationAreaResource().build().getType(), INNOVATION_AREA);
    }

    @Test
    public void isNotNone() {
        assertTrue(newInnovationAreaResource().withName("Foo").build().isNotNone());
        assertFalse(newInnovationAreaResource().withName("None").build().isNotNone());
    }
}