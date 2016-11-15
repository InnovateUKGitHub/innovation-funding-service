package com.worth.ifs.category.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Class for testing {@link CategoryLinkResource}
 */
public class CategoryLinkResourceTest {
    CategoryLinkResource categoryLink;
    private Long id;

    private Long category;
    private String className;
    private Long classPk;

    @Before
    public void setUp() throws Exception {
        id = 1L;
        category = 2L;
        className = "SomeClass";
        classPk = 4L;

        categoryLink = new CategoryLinkResource(id, category, className, classPk);
    }

    @Test
    public void categoryShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(categoryLink.getId(), id);
        Assert.assertEquals(categoryLink.getCategory(), category);
        Assert.assertEquals(categoryLink.getClassName(), className);
        Assert.assertEquals(categoryLink.getClassPk(), classPk);
    }

}
