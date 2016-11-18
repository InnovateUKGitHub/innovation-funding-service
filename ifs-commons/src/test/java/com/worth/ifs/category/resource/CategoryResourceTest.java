package com.worth.ifs.category.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.newHashSet;

/**
 * Class for testing {@link CategoryResource}
 */
public class CategoryResourceTest {
    CategoryResource category;
    private Long id;

    private String name;
    private CategoryType categoryType;
    private Long parent;
    private Set<Long> children;

    @Before
    public void setUp() throws Exception {
        id = 1L;
        name = "New Category";
        categoryType = CategoryType.INNOVATION_AREA;
        parent = 4L;
        children = newHashSet();
        children.add(2L);
        children.add(3L);

        category = new CategoryResource(id, name, categoryType, parent, children);
        category.setId(id);
    }

    @Test
    public void categoryShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(category.getId(), id);
        Assert.assertEquals(category.getName(), name);
        Assert.assertEquals(category.getType(), categoryType);
        Assert.assertEquals(category.getParent(), parent);
        Assert.assertEquals(category.getChildren(), children);
    }

}
