package org.innovateuk.ifs.category.resource;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
import static org.junit.Assert.assertEquals;

/**
 * Class for testing {@link CategoryResource}
 */
public class CategoryResourceTest {
    CategoryResource category;
    private Long id;

    private String name;
    private CategoryType categoryType;
    private Long parent;
    private List<CategoryResource> children;

    @Before
    public void setUp() throws Exception {
        id = 1L;
        name = "Innovation Sector";
        categoryType = INNOVATION_SECTOR;
        parent = 4L;
        children = asList(
                new CategoryResource(2L, "Innovation Area 1", INNOVATION_AREA, 1L),
                new CategoryResource(3L, "Innovation Area 2", INNOVATION_AREA, 1L)
        );

        category = new CategoryResource(id, name, categoryType, parent, children);
    }

    @Test
    public void categoryShouldReturnCorrectAttributeValues() throws Exception {
        assertEquals(category.getId(), id);
        assertEquals(category.getName(), name);
        assertEquals(category.getType(), categoryType);
        assertEquals(category.getParent(), parent);
        assertEquals(category.getChildren(), children);
    }
}
