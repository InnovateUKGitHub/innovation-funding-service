package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.CategoryLink;
import org.innovateuk.ifs.category.resource.CategoryType;
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
        List<CategoryLink> found = repository.findByClassNameAndClassPk("org.innovateuk.ifs.competition.domain.Competition", 7L);

        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals("org.innovateuk.ifs.competition.domain.Competition", found.get(0).getClassName());
        assertEquals(Long.valueOf(1L), found.get(0).getCategory().getId());
    }

    @Test
    public void test_findByClassNameAndClassPkAndCategory_Type() throws Exception {
        List<CategoryLink> found  = repository.findByClassNameAndClassPkAndCategory_Type("org.innovateuk.ifs.competition.domain.Competition", 7L, CategoryType.INNOVATION_SECTOR);

        assertEquals(1, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals("org.innovateuk.ifs.competition.domain.Competition", found.get(0).getClassName());
        assertEquals(Long.valueOf(1L), found.get(0).getCategory().getId());
    }

}
