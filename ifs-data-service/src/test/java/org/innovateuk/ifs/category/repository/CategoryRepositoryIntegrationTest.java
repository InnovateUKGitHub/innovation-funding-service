package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.junit.Assert.assertEquals;

@Ignore
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

//    @Test
//    public void findByType() throws Exception {
//        List<InnovationSector> found = repository.findInnovationSectors();
//
//        assertEquals(4, found.size());
//        assertEquals(Long.valueOf(4L), found.get(3).getId());
//    }

//    @Test
//    public void findByTypeOrderByNameAsc() throws Exception {
//        List<InnovationSector> found = repository.findInnovationSectorsOrderByNameAsc();
//
//        assertEquals(4, found.size());
//        assertEquals("Emerging and enabling technologies", found.get(0).getName());
//        assertEquals("Health and life sciences", found.get(1).getName());
//        assertEquals("Infrastructure systems", found.get(2).getName());
//        assertEquals("Materials and manufacturing", found.get(3).getName());
//    }
//
//    @Test
//    public void findByIdAndType() {
//        String innovationAreaName = "Machine learning";
//        InnovationArea savedCategory = repository.save(newInnovationArea().withName(innovationAreaName).build());
//
//        InnovationArea retrievedCategory = repository.findOneInnovationArea(savedCategory.getId());
//
//        assertEquals(savedCategory, retrievedCategory);
//    }
//
//    @Test
//    public void findByNameAndType() {
//        String innovationAreaName = "Machine learning";
//        InnovationArea savedCategory = repository.save(newInnovationArea().withName(innovationAreaName).build());
//
//        InnovationArea retrievedCategory = repository.findInnovationAreaByName(innovationAreaName);
//
//        assertEquals(savedCategory, retrievedCategory);
//    }
}
