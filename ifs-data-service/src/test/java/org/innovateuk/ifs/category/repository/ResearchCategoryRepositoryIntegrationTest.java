package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.junit.Assert.assertEquals;

public class ResearchCategoryRepositoryIntegrationTest
        extends BaseRepositoryIntegrationTest<ResearchCategoryRepository> {

    @Autowired
    @Override
    protected void setRepository(final ResearchCategoryRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    @Before
    public void setup() {
        grantClaimMaximumRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    public void findById() {
        ResearchCategory researchCategory = repository.save(newResearchCategory()
                .with(id(null))
                .withName("Category Name")
                .build());
        flushAndClearSession();

        ResearchCategory actual = repository.findById(researchCategory.getId());
        assertEquals(researchCategory, actual);
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
        List<ResearchCategory> researchCategories = newResearchCategory()
                .with(id(null))
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
                .with(id(null))
                .withName("bbb", "aaa", "ccc")
                .withPriority(2, 1, 3)
                .build(3);

        repository.save(innovationAreas);
        flushAndClearSession();

        List<ResearchCategory> expectedInnovationAreas = newResearchCategory()
                .with(id(null))
                .withName("aaa", "bbb", "ccc")
                .build(3);

        List<ResearchCategory> actual = repository.findAllByOrderByPriorityAsc();

        assertEquals(expectedInnovationAreas, actual);
    }
}