package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.CategoryBuilder.newCategory;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
import static org.junit.Assert.assertEquals;

public class CategoryBuilderTest {
    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedName = "Internet of Things";
        CategoryType expectedType = INNOVATION_AREA;

        Category category = newCategory()
                .withId(expectedId)
                .withName(expectedName)
                .withType(expectedType)
                .build();

        assertEquals(expectedId, category.getId());
        assertEquals(expectedName, category.getName());
        assertEquals(expectedType, category.getType());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 7L, 13L };
        String[] expectedNames = { "Internet of Things", "Manufacturing" };
        CategoryType[] expectedTypes = { INNOVATION_AREA, INNOVATION_SECTOR };

        List<Category> categories = newCategory()
                .withId(expectedIds)
                .withName(expectedNames)
                .withType(expectedTypes)
                .build(2);

        Category first = categories.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedTypes[0], first.getType());

        Category second = categories.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedTypes[1], second.getType());
    }
}
