package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
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
        InnovationAreaResource cat1 = newInnovationAreaResource()
                .withId(1L)
                .withName("Category 1")
                .withParent(1L)
                .build();

        InnovationAreaResource cat2 = newInnovationAreaResource()
                .withId(3L)
                .withParent(1L)
                .withName("Category 2")
                .build();

        final List<InnovationAreaResource> expected = new ArrayList<>(asList(cat1, cat2));

        when(categoryRestService.getInnovatationAreasBySector(1L)).thenReturn(restSuccess(expected));

        final List<InnovationAreaResource> found = service.getInnovationAreasBySector(1L);
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(2L), found.get(0).getId());
        assertEquals(Long.valueOf(3L), found.get(1).getId());
    }

    @Test
    public void test_getCategoryByType() throws Exception {
        InnovationAreaResource cat1 = newInnovationAreaResource()
                .withId(2L)
                .withName("Category 1")
                .withParent(1L)
                .build();

        InnovationAreaResource cat2 = newInnovationAreaResource()
                .withId(3L)
                .withParent(1L)
                .withName("Category 2")
                .build();


        final List<InnovationAreaResource> expected = new ArrayList<>(asList(cat1, cat2));

        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(expected));

        final List<InnovationAreaResource> found = service.getInnovationAreas();
        assertEquals(2, found.size());
        assertEquals(CategoryType.INNOVATION_AREA, found.get(0).getType());
        assertEquals(CategoryType.INNOVATION_AREA, found.get(1).getType());
    }
}
