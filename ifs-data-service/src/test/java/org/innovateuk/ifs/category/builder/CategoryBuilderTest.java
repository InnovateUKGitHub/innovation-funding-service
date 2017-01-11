package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.junit.Assert.assertEquals;

// todo rename to InnovationAreaBuilderTest
// todo create tests for other category builders
public class CategoryBuilderTest {
    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedName = "Internet of Things";

        InnovationArea category = newInnovationArea()
                .withId(expectedId)
                .withName(expectedName)
                .build();

        assertEquals(expectedId, category.getId());
        assertEquals(expectedName, category.getName());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 7L, 13L };
        String[] expectedNames = { "Internet of Things", "Manufacturing" };

        List<InnovationArea> categories = newInnovationArea()
                .withId(expectedIds)
                .withName(expectedNames)
                .build(2);

        Category first = categories.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());

        Category second = categories.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
    }
}
