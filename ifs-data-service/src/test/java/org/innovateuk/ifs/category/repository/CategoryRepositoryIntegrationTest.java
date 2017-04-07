package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.Category;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.junit.Assert.assertEquals;

public class CategoryRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CategoryRepository> {

    @Autowired
    @Override
    protected void setRepository(final CategoryRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findAll() {
        List<Category> categories = asList(
                repository.save(newInnovationArea().withName("name").with(id(null)).build()),
                repository.save(newInnovationSector().withName("name").with(id(null)).build()),
                repository.save(newResearchCategory().withName("name").with(id(null)).build())
        );

        flushAndClearSession();

        List<Category> found = repository.findAll(categories.stream().map(Category::getId).collect(Collectors.toList()));

        assertEquals(categories, found);
    }
}
