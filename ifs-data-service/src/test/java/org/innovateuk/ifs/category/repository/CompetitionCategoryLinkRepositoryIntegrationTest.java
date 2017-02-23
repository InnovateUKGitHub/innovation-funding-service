package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompetitionCategoryLinkRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CompetitionCategoryLinkRepository> {

    @Autowired
    @Override
    protected void setRepository(final CompetitionCategoryLinkRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByCompetitionId() throws Exception {
        List<CompetitionCategoryLink> found = repository.findByCompetitionId(7L);

        assertEquals(2, found.size());

        CompetitionCategoryLink first = found.get(0);
        assertEquals(1L, (long) first.getId());
        assertEquals(7L, (long) first.getEntity().getId());
        assertEquals(1L, (long) first.getCategory().getId());
    }

    @Test
    public void findAllByCompetitionIdAndCategory_Type() throws Exception {
        List<CompetitionCategoryLink> found  = repository.findAllByCompetitionIdAndCategoryType(7L, CategoryType.INNOVATION_SECTOR);

        assertEquals(1, found.size());
        assertEquals(1L, (long) found.get(0).getId());
        assertEquals(7L, (long) found.get(0).getEntity().getId());
        assertEquals(1L, (long) found.get(0).getCategory().getId());
    }
}
