package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.category.service.CategoryRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;


/**
 * Test Class for all functionality in {@link CategoryServiceImpl}
 */
public class CategoryServiceImplTest extends BaseServiceUnitTest<CategoryService> {

    @Mock
    private CategoryRestService categoryRestService;

    @Override
    protected CategoryService supplyServiceUnderTest() {
        return new CategoryServiceImpl();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

    }

    @Test
    public void test_getCategoryByParentId() throws Exception {
        CategoryResource cat1 = new CategoryResource();
        cat1.setType(CategoryType.INNOVATION_AREA);
        cat1.setName("Category 1");
        cat1.setParent(1L);
        cat1.setId(2L);

        CategoryResource cat2 = new CategoryResource();
        cat2.setType(CategoryType.INNOVATION_AREA);
        cat2.setName("Category 2");
        cat2.setParent(1L);
        cat2.setId(3L);


        final List<CategoryResource> expected = new ArrayList<>(asList(cat1, cat2));

        when(categoryRestService.getByParent(1L)).thenReturn(restSuccess(expected));

        final List<CategoryResource> found = service.getCategoryByParentId(1L);
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(2L), found.get(0).getId());
        assertEquals(Long.valueOf(3L), found.get(1).getId());
    }

    @Test
    public void test_getCategoryByType() throws Exception {
        CategoryResource cat1 = new CategoryResource();
        cat1.setType(CategoryType.INNOVATION_AREA);
        cat1.setName("Category 1");
        cat1.setParent(1L);
        cat1.setId(2L);

        CategoryResource cat2 = new CategoryResource();
        cat2.setType(CategoryType.INNOVATION_AREA);
        cat2.setName("Category 2");
        cat2.setParent(1L);
        cat2.setId(3L);


        final List<CategoryResource> expected = new ArrayList<>(asList(cat1, cat2));

        when(categoryRestService.getByType(CategoryType.INNOVATION_AREA)).thenReturn(restSuccess(expected));

        final List<CategoryResource> found = service.getCategoryByType(CategoryType.INNOVATION_AREA);
        assertEquals(2, found.size());
        assertEquals(CategoryType.INNOVATION_AREA, found.get(0).getType());
        assertEquals(CategoryType.INNOVATION_AREA, found.get(1).getType());
    }


}