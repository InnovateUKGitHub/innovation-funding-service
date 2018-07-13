package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.assertEquals;

public class ResearchCategoryRepositoryIntegrationTest
        extends BaseRepositoryIntegrationTest<ResearchCategoryRepository> {

    @Autowired
    @Override
    protected void setRepository(final ResearchCategoryRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByName() {
        ResearchCategory researchCategory = repository.save(newResearchCategory()
                .with(id(null))
                .withName("Category Name")
                .build());
        flushAndClearSession();

        ResearchCategory actual = repository.findByName(researchCategory.getName());
        assertEquals(researchCategory, actual);
    }


    @Test
    public void findAll() {
        List<ResearchCategory> researchCategories = repository.findAll();

        assertEquals(3, researchCategories.size());

        List<ResearchCategory> expectedResearchCategories = newResearchCategory()
                .withName("Feasibility studies", "Industrial research", "Experimental development")
                .withPriority(0, 1, 2)
                .build(3);

        forEachWithIndex(expectedResearchCategories, (index, expected) -> {
            assertThat(researchCategories.get(index))
                    .isEqualToComparingOnlyGivenFields(expected, "name", "priority");
        });
    }

    @Test
    public void findAllByOrderByPriorityAsc() {
        List<ResearchCategory> researchCategories = repository.findAllByOrderByPriorityAsc();

        assertEquals(3, researchCategories.size());

        List<ResearchCategory> expectedResearchCategories = newResearchCategory()
                .withName("Feasibility studies", "Industrial research", "Experimental development")
                .withPriority(0, 1, 2)
                .build(3);

        forEachWithIndex(expectedResearchCategories, (index, expected) -> {
            assertThat(researchCategories.get(index))
                    .isEqualToComparingOnlyGivenFields(expected, "name", "priority");
        });
    }
}