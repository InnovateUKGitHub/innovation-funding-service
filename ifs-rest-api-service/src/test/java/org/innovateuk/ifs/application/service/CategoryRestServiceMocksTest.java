package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestServiceImpl;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.innovationAreaResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.innovationSectorResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.researchCategoryResourceListType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests to check the CategoryRestService's interaction with the RestTemplate and the processing of its results
 */
public class CategoryRestServiceMocksTest extends BaseRestServiceUnitTest<CategoryRestServiceImpl> {

    private static final String categoryRestURL = "/category";

    @Override
    protected CategoryRestServiceImpl registerRestServiceUnderTest() {
        return new CategoryRestServiceImpl();
    }

    @Test
    public void getInnovationAreas() {
        String expectedUrl = categoryRestURL + "/findInnovationAreas";
        List<InnovationAreaResource> returnedCategoryResources = newInnovationAreaResource().build(3);

        setupGetWithRestResultExpectations(expectedUrl, innovationAreaResourceListType(), returnedCategoryResources);

        List<InnovationAreaResource> categoryResources = service.getInnovationAreas().getSuccessObjectOrThrowException();

        assertNotNull(categoryResources);
        assertEquals(returnedCategoryResources, categoryResources);
    }

    @Test
    public void getInnovationSectors() {
        String expectedUrl = categoryRestURL + "/findInnovationSectors";
        List<InnovationSectorResource> returnedCategoryResources = newInnovationSectorResource().build(3);

        setupGetWithRestResultExpectations(expectedUrl, innovationSectorResourceListType(), returnedCategoryResources);

        List<InnovationSectorResource> categoryResources = service.getInnovationSectors().getSuccessObjectOrThrowException();

        assertNotNull(categoryResources);
        assertEquals(returnedCategoryResources, categoryResources);
    }

    @Test
    public void getResearchCategories() {
        String expectedUrl = categoryRestURL + "/findResearchCategories";
        List<ResearchCategoryResource> returnedCategoryResources = newResearchCategoryResource().build(3);

        setupGetWithRestResultExpectations(expectedUrl, researchCategoryResourceListType(), returnedCategoryResources);

        List<ResearchCategoryResource> categoryResources = service.getResearchCategories().getSuccessObjectOrThrowException();

        assertNotNull(categoryResources);
        assertEquals(returnedCategoryResources, categoryResources);
    }

    @Test
    public void getInnovationAreasBySector() {
        String expectedUrl = categoryRestURL + "/findByInnovationSector/1";
        List<InnovationAreaResource> returnedCategoryResources = newInnovationAreaResource().build(3);
        setupGetWithRestResultExpectations(expectedUrl, innovationAreaResourceListType(), returnedCategoryResources);

        List<InnovationAreaResource> categoryResources = service.getInnovationAreasBySector(1L).getSuccessObjectOrThrowException();

        assertNotNull(categoryResources);
        assertEquals(returnedCategoryResources, categoryResources);
    }
}
