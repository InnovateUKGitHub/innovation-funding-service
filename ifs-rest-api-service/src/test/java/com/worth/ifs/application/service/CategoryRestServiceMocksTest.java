package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.category.service.CategoryRestServiceImpl;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.category.builder.CategoryResourceBuilder.newCategoryResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.categoryResourceListType;
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
    public void test_findByType() {

        String expectedUrl = categoryRestURL + "/findByType/" + CategoryType.INNOVATION_AREA.getName();
        List<CategoryResource> returnedCategoryResources = CategoryResourceBuilder.newCategoryResource().build(3);
        setupGetWithRestResultExpectations(expectedUrl, ParameterizedTypeReferences.categoryResourceListType(), returnedCategoryResources);

        // now run the method under test
        List<CategoryResource> categoryResources = service.getByType(CategoryType.INNOVATION_AREA).getSuccessObjectOrThrowException();
        assertNotNull(categoryResources);
        assertEquals(returnedCategoryResources, categoryResources);
    }

    @Test
    public void test_findByParent() {

        String expectedUrl = categoryRestURL + "/findByParent/1";
        List<CategoryResource> returnedCategoryResources = CategoryResourceBuilder.newCategoryResource().build(3);
        setupGetWithRestResultExpectations(expectedUrl, ParameterizedTypeReferences.categoryResourceListType(), returnedCategoryResources);

        // now run the method under test
        List<CategoryResource> categoryResources = service.getByParent(1L).getSuccessObjectOrThrowException();
        assertNotNull(categoryResources);
        assertEquals(returnedCategoryResources, categoryResources);
    }
}
