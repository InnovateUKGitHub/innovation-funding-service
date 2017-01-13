package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestServiceImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.innovationAreaResourceListType;
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

    // todo rename the test methdds
    // todo test the other reset service methods

    @Test
    public void test_findByType() {

        String expectedUrl = categoryRestURL + "/findInnovationAreas";
        List<InnovationAreaResource> returnedCategoryResources = Arrays.asList(1,2,3).stream().map(i -> new InnovationAreaResource()).collect(Collectors.toList());

        setupGetWithRestResultExpectations(expectedUrl, innovationAreaResourceListType(), returnedCategoryResources);

        // now run the method under test
        List<? extends CategoryResource> categoryResources = service.getInnovationAreas().getSuccessObjectOrThrowException();
        assertNotNull(categoryResources);
        assertEquals(returnedCategoryResources, categoryResources);
    }

    @Test
    public void test_findByParent() {

        String expectedUrl = categoryRestURL + "/findByInnovationSector/1";
        List<InnovationAreaResource> returnedCategoryResources = Arrays.asList(1,2,3).stream().map(i -> new InnovationAreaResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(expectedUrl, innovationAreaResourceListType(), returnedCategoryResources);

        // now run the method under test
        List<? extends CategoryResource> categoryResources = service.getInnovatationAreasBySector(1L).getSuccessObjectOrThrowException();
        assertNotNull(categoryResources);
        assertEquals(returnedCategoryResources, categoryResources);
    }
}
