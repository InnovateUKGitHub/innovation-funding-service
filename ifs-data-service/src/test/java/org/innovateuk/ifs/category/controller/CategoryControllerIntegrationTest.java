package org.innovateuk.ifs.category.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
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
    public void getCategoriesByTypeArea() {
        RestResult<List<CategoryResource>> categoriesResult = controller.findByType(INNOVATION_AREA.getName());
        assertTrue(categoriesResult.isSuccess());
        List<CategoryResource> categories = categoriesResult.getSuccessObject();

        assertThat(categories, hasSize(28));
        assertThat(categories, everyItem(hasProperty("type", equalTo(INNOVATION_AREA))));
        assertThat(categories, everyItem(hasProperty("children", hasSize(0))));
    }

    @Test
    public void getCategoriesByTypeSector() {
        RestResult<List<CategoryResource>> categoriesResult = controller.findByType(INNOVATION_SECTOR.getName());
        assertTrue(categoriesResult.isSuccess());
        List<CategoryResource> categories = categoriesResult.getSuccessObject();

        assertThat(categories, hasSize(4));
        assertThat(categories, everyItem(hasProperty("type", equalTo(INNOVATION_SECTOR))));
        assertThat(categories, everyItem(hasProperty("children", everyItem(hasProperty("type", equalTo(INNOVATION_AREA))))));
        assertThat(categories, everyItem(hasProperty("children", everyItem(hasProperty("children", hasSize(0))))));
    }

    @Test
    public void getCategoriesByParent() {
        RestResult<List<CategoryResource>> categoriesResult = controller.findByParent(1L);
        assertTrue(categoriesResult.isSuccess());
        List<CategoryResource> categories = categoriesResult.getSuccessObject();

        assertThat(categories, hasSize(6));
        assertThat(categories, everyItem(hasProperty("parent", equalTo(1L))));
        assertThat(categories, containsInAnyOrder(asList(
                hasProperty("name", equalTo("Advanced Therapies")),
                hasProperty("name", equalTo("Precision Medicine")),
                hasProperty("name", equalTo("Medicines Technology")),
                hasProperty("name", equalTo("Bioscience")),
                hasProperty("name", equalTo("Agri Productivity")),
                hasProperty("name", equalTo("Enhanced Food Quality"))
        )));
    }
}
