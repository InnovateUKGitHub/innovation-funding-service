package com.worth.ifs.category.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.resource.CategoryType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CategoryRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CategoryRepository> {

    @Autowired
    @Override
    protected void setRepository(final CategoryRepository repository) {
        this.repository = repository;
    }


    @Test
    public void test_findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk() throws Exception {
        Category found = repository.findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(CategoryType.INNOVATION_SECTOR, "com.worth.ifs.competition.domain.Competition", 7L);

        assertEquals(Long.valueOf(1L), found.getId());
        assertEquals(CategoryType.INNOVATION_SECTOR, found.getType());
        assertEquals("Health and life sciences", found.getName());
    }

    @Test
    public void test_findByType() throws Exception {
        List<Category> found = repository.findByType(CategoryType.INNOVATION_SECTOR);

        assertEquals(4, found.size());
        assertEquals(Long.valueOf(4L), found.get(3).getId());
    }

}
