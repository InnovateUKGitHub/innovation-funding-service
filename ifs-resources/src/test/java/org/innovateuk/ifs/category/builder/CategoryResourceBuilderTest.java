package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.category.builder.CategoryResourceBuilder.newCategoryResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
import static org.junit.Assert.*;

public class CategoryResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        long id = 1L;
        long parentId = 2L;
        String name = "Test Category";
        CategoryType type = INNOVATION_AREA;
        List<CategoryResource> children = emptyList();

        CategoryResource category = newCategoryResource()
                .withId(id)
                .withName(name)
                .withType(INNOVATION_AREA)
                .withChildren(children)
                .withParent(parentId)
                .build();

        assertEquals(id, category.getId().longValue());
        assertEquals(name, category.getName());
        assertEquals(parentId, category.getParent().longValue());
        assertEquals(children, category.getChildren());
        assertEquals(type, category.getType());
    }

    @Test
    public void buildMany() throws Exception {
        Long[] ids = {1L, 2L};
        Long[] parentIds = {2L, 0L};
        String[] names = {"Test Innovation Area", "Test Innovation Sector"};
        CategoryType[] types = {INNOVATION_AREA, INNOVATION_SECTOR};

        List<CategoryResource> children1 = emptyList();
        List<CategoryResource> children2 = emptyList();

        List<CategoryResource> categoryResources = newCategoryResource()
                .withId(ids)
                .withName(names)
                .withType(types)
                .withParent(parentIds)
                .withChildren(children1, children2)
                .build(2);

        assertEquals(ids[0], categoryResources.get(0).getId());
        assertEquals(parentIds[0], categoryResources.get(0).getParent());
        assertEquals(names[0], categoryResources.get(0).getName());
        assertEquals(types[0], categoryResources.get(0).getType());
        assertEquals(children1, categoryResources.get(0).getChildren());

        assertEquals(ids[1], categoryResources.get(1).getId());
        assertEquals(parentIds[1], categoryResources.get(1).getParent());
        assertEquals(names[1], categoryResources.get(1).getName());
        assertEquals(types[1], categoryResources.get(1).getType());
        assertEquals(children2, categoryResources.get(1).getChildren());
    }
}
