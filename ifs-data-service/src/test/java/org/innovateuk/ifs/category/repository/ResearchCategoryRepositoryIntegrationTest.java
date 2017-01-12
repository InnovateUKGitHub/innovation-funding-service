package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.junit.Assert.assertEquals;

public class ResearchCategoryRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ResearchCategoryRepository> {

    @Autowired
    @Override
    protected void setRepository(final ResearchCategoryRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private CompetitionCategoryLinkRepository competitionCategoryLinkRepository;

    @Before
    public void setup() {
        competitionCategoryLinkRepository.deleteAll(); // delete links to avoid fk constraint issues
        repository.deleteAll();
    }

    @Test
    public void findById() {
        ResearchCategory researchCategory = repository.save(newResearchCategory().withId(null).withName("Category Name").build());
        flushAndClearSession();

        ResearchCategory actual = repository.findById(researchCategory.getId());
        assertEquals(researchCategory, actual);
    }

    @Test
    public void findAll() {
        List<ResearchCategory> researchCategories = newResearchCategory()
                .withName("bbb", "aaa", "ccc")
                .build(3);

        repository.save(researchCategories);
        flushAndClearSession();

        List<ResearchCategory> actual = repository.findAll();

        assertEquals(researchCategories, actual);
    }

    @Test
    public void findAllByOrderByNameAsc() {
        List<ResearchCategory> innovationAreas = newResearchCategory()
                .withName("bbb", "aaa", "ccc")
                .build(3);

        repository.save(innovationAreas);
        flushAndClearSession();

        List<ResearchCategory> expectedInnovationAreas = newResearchCategory()
                .withName("aaa", "bbb", "ccc")
                .build(3);

        List<ResearchCategory> actual = repository.findAllByOrderByNameAsc();

        assertEquals(expectedInnovationAreas, actual);
    }
}