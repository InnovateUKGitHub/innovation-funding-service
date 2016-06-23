package com.worth.ifs.category.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.category.domain.CategoryLink;
import com.worth.ifs.category.resource.CategoryType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CategoryLinkRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CategoryLinkRepository> {

    @Autowired
    @Override
    protected void setRepository(final CategoryLinkRepository repository) {
        this.repository = repository;
    }


    @Test
    public void test_findByClassNameAndClassPk() throws Exception {
        List<CategoryLink> found = repository.findByClassNameAndClassPk("com.worth.ifs.competition.domain.Competition", 7L);

        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals("com.worth.ifs.competition.domain.Competition", found.get(0).getClassName());
        assertEquals(Long.valueOf(1L), found.get(0).getCategory().getId());
    }

    @Test
    public void test_findByClassNameAndClassPkAndCategory_Type() throws Exception {
        CategoryLink found  = repository.findByClassNameAndClassPkAndCategory_Type("com.worth.ifs.competition.domain.Competition", 7L, CategoryType.INNOVATION_SECTOR);

        assertEquals(Long.valueOf(1L), found.getId());
        assertEquals("com.worth.ifs.competition.domain.Competition", found.getClassName());
        assertEquals(Long.valueOf(1L), found.getCategory().getId());
    }

}
