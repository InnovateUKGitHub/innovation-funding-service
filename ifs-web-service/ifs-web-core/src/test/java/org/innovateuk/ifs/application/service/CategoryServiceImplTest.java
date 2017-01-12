package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
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

    @Test
    public void getInnovationAreasBySector() throws Exception {
        List<InnovationAreaResource> expected = newInnovationAreaResource()
                .withId(1L, 3L)
                .withName("Category 1", "Category 2")
                .withParent(1L, 1L)
                .build(2);

        when(categoryRestService.getInnovatationAreasBySector(1L)).thenReturn(restSuccess(expected));

        final List<InnovationAreaResource> actual = service.getInnovationAreasBySector(1L);
        assertEquals(expected, actual);
    }

    @Test
    public void getInnovationAreas() throws Exception {
        List<InnovationAreaResource> expected = newInnovationAreaResource()
                .withId(2L, 3L)
                .withName("Category 1", "Category 2")
                .withParent(1L, 1L)
                .build(2);

        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(expected));

        final List<InnovationAreaResource> actual = service.getInnovationAreas();

        assertEquals(expected, actual);
    }

    @Test
    public void getInnovationSectors() throws Exception {
        List<InnovationSectorResource> expected = newInnovationSectorResource()
                .withId(2L, 3L)
                .withName("Sector 1", "Sector 2")
                .withChildren(
                        newInnovationAreaResource().build(2),
                        newInnovationAreaResource().build(2)
                )
                .build(2);

        when(categoryRestService.getInnovationSectors()).thenReturn(restSuccess(expected));

        final List<InnovationSectorResource> actual = service.getInnovationSectors();

        assertEquals(expected, actual);
    }

    @Test
    public void getResearchCategories() throws Exception {
        List<ResearchCategoryResource> expected = newResearchCategoryResource()
                .withId(2L, 3L)
                .withName("Research Sector 1", "Research Sector 2")
                .build(2);

        when(categoryRestService.getResearchCategories()).thenReturn(restSuccess(expected));

        final List<ResearchCategoryResource> actual = service.getResearchCategories();

        assertEquals(expected, actual);
    }
}
