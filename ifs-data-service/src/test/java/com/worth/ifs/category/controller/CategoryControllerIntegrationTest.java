package com.worth.ifs.category.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.rest.RestResult;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

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
    public void testGetCategoriesByTypeArea(){
        RestResult<List<CategoryResource>> categoriesResult = controller.findByType(CategoryType.INNOVATION_AREA.getName());
        assertTrue(categoriesResult.isSuccess());
        List<CategoryResource> categories = categoriesResult.getSuccessObject();

        assertThat(categories, hasSize(9));
        assertThat(categories, everyItem(Matchers.hasProperty("type", equalTo(CategoryType.INNOVATION_AREA))));
    }

    @Test
    public void testGetCategoriesByTypeSector(){
        RestResult<List<CategoryResource>> categoriesResult = controller.findByType(CategoryType.INNOVATION_SECTOR.getName());
        assertTrue(categoriesResult.isSuccess());
        List<CategoryResource> categories = categoriesResult.getSuccessObject();

        assertThat(categories, hasSize(4));
        assertThat(categories, everyItem(Matchers.hasProperty("type", equalTo(CategoryType.INNOVATION_SECTOR))));
    }
}
