package org.innovateuk.ifs.category.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Rollback
@Transactional
public class CategoryControllerIntegrationTest extends BaseControllerIntegrationTest<CategoryController> {
    @Override
    @Autowired
    protected void setControllerUnderTest(CategoryController controller) {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Test
    public void findInnovationAreas() {
        RestResult<List<InnovationAreaResource>> categoriesResult = controller.findInnovationAreas();
        assertTrue(categoriesResult.isSuccess());
        List<InnovationAreaResource> categories = categoriesResult.getSuccessObject();

        assertThat(categories, hasSize(38));
        assertThat(categories, everyItem(hasProperty("sector", notNullValue())));
    }

    @Test
    public void findInnovationSectors() {
        RestResult<List<InnovationSectorResource>> categoriesResult = controller.findInnovationSectors();
        assertTrue(categoriesResult.isSuccess());
        List<InnovationSectorResource> categories = categoriesResult.getSuccessObject();

        assertThat(categories, hasSize(4));
        assertThat(categories, everyItem(hasProperty("children", notNullValue())));
    }

    @Test
    public void findResearchCategories() {
        RestResult<List<ResearchCategoryResource>> categoriesResult = controller.findResearchCategories();
        assertTrue(categoriesResult.isSuccess());
        List<ResearchCategoryResource> categories = categoriesResult.getSuccessObject();

        assertThat(categories, hasSize(3));
    }

    @Test
    public void findInnovationAreasBySector() {
        RestResult<List<InnovationAreaResource>> categoriesResult = controller.findInnovationAreasBySector(1L);
        assertTrue(categoriesResult.isSuccess());
        List<InnovationAreaResource> categories = categoriesResult.getSuccessObject();

        assertThat(categories, hasSize(8));
        assertThat(categories, everyItem(hasProperty("sector", equalTo(1L))));
        assertThat(categories, containsInAnyOrder(asList(
                hasProperty("name", equalTo("Advanced therapies")),
                hasProperty("name", equalTo("Biosciences")),
                hasProperty("name", equalTo("Diagnostics, medical technology and devices")),
                hasProperty("name", equalTo("Digital health")),
                hasProperty("name", equalTo("Independent living and wellbeing")),
                hasProperty("name", equalTo("Precision medicine")),
                hasProperty("name", equalTo("Preclinical technologies and drug target discovery")),
                hasProperty("name", equalTo("Therapeutic and medicine development"))
        )));
    }
}
