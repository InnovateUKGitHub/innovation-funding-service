package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.Category;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.category.builder.CategoryBuilder.newCategory;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
import static org.junit.Assert.assertEquals;

public class CategoryRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<CategoryRepository> {

    @Autowired
    @Override
    protected void setRepository(final CategoryRepository repository) {
        this.repository = repository;
    }


//    @Test
//    public void findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk() throws Exception {
//        Category found = repository.findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(INNOVATION_SECTOR, "org.innovateuk.ifs.competition.domain.Competition", 7L);
//
//        assertEquals(Long.valueOf(1L), found.getId());
//        assertEquals(INNOVATION_SECTOR, found.getType());
//        assertEquals("Health and life sciences", found.getName());
//    }

    @Test
    public void findByType() throws Exception {
        List<Category> found = repository.findByType(INNOVATION_SECTOR);

        assertEquals(4, found.size());
        assertEquals(Long.valueOf(4L), found.get(3).getId());
    }

    @Test
    public void findByTypeOrderByNameAsc() throws Exception {
        List<Category> found = repository.findByTypeOrderByNameAsc(INNOVATION_SECTOR);

        assertEquals(4, found.size());
        assertEquals("Emerging and enabling technologies", found.get(0).getName());
        assertEquals("Health and life sciences", found.get(1).getName());
        assertEquals("Infrastructure systems", found.get(2).getName());
        assertEquals("Materials and manufacturing", found.get(3).getName());
    }

    @Test
    public void findByIdAndType() {
        String innovationAreaName = "Machine learning";
        Category savedCategory = repository.save(newCategory().withType(INNOVATION_AREA).withName(innovationAreaName).build());

        Category retrievedCategory = repository.findByIdAndType(savedCategory.getId(), INNOVATION_AREA);

        assertEquals(savedCategory, retrievedCategory);
    }

    @Test
    public void findByNameAndType() {
        String innovationAreaName = "Machine learning";
        Category savedCategory = repository.save(newCategory().withType(INNOVATION_AREA).withName(innovationAreaName).build());

        Category retrievedCategory = repository.findByNameAndType(innovationAreaName, INNOVATION_AREA);

        assertEquals(savedCategory, retrievedCategory);
    }
}
